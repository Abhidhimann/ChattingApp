package com.example.chattingApp.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.data.local.dao.AIChatMessageDao
import com.example.chattingApp.data.remote.dto.AIChatRequestBody
import com.example.chattingApp.data.remote.dto.AIQuery
import com.example.chattingApp.data.remote.services.singlechat.SingleChatService
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.services.aichat.AiChatService
import com.example.chattingApp.data.remote.services.chatsocket.ChatSocketService
import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.AIChatMessageType
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.domain.repository.ChatRepository
import com.example.chattingApp.utils.AIChatBotException
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.tempTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val singleChatService: SingleChatService,
    private val chatSocketService: ChatSocketService,
    private val aiChatService: AiChatService,
    private val aiChatMessageDao: AIChatMessageDao,
    private val appPrefs: SharedPreferences
) : ChatRepository {

    // todo later change it to db
    private fun getUser(): UserSummary? {
        val userId = appPrefs.getString("user_id", "")
        val profileImageUrl = appPrefs.getString("profile_url", "")
        val name = appPrefs.getString("user_name", "")
        if (userId == null || profileImageUrl == null || name == null) return null
        return UserSummary(name = name, profileImageUrl = profileImageUrl, userId = userId)
    }

    private suspend fun getSingleChat(chatId: String): ResultResponse<SingleChatResponse> =
        withContext(Dispatchers.IO) {
            val selfUser = getUser()
            if (selfUser == null) {
                Log.i(tempTag(), "Error in getting user from prefs")
                return@withContext ResultResponse.Failed(Exception("Error in getting user from prefs"))
            }
            singleChatService.getSingleChat(chatId)
        }

    override suspend fun observeMessages(conversationId: String) = withContext(Dispatchers.IO) {
        val selfUser = getUser()
        if (selfUser == null) {
            Log.i(tempTag(), "Error in getting user from prefs")
            return@withContext emptyFlow()
        }
        chatSocketService.observeMessages(conversationId).map {
            it.toMessage(selfUser.userId)
        }
    }

    override suspend fun sendMessage(message: Message) = withContext(Dispatchers.IO) {
        chatSocketService.sendMessage(message.toMessageDto())
    }

    override suspend fun getChatBotResponse(aiChatMessages: List<AIChatMessage>): ResultResponse<Unit> =
        withContext(Dispatchers.IO) {
            aiChatMessageDao.insertAIChatMessage(aiChatMessages.last().toAiChatMessageEntity())
            val chatRequest = AIChatRequestBody(
                model = "meta-llama/Meta-Llama-3.1-8B-Instruct-lora",
                messages = aiChatMessages.map { it.toAiQuery() }
            )
            when (val result = getChatBotResponse(chatRequest)) {
                is ResultResponse.Success -> {
                    aiChatMessageDao.insertAIChatMessage(result.data.toAiChatMessageEntity())
                    return@withContext ResultResponse.Success(Unit)
                }

                is ResultResponse.Failed -> {
                    return@withContext ResultResponse.Failed(result.exception)
                }
            }
        }

    override suspend fun summarizeConversation(messages: List<Message>): ResultResponse<AIChatMessage> {
        return withContext(Dispatchers.IO) {
            val selfUser = getUser()
            if (selfUser == null) {
                Log.i(tempTag(), "Error in getting user from prefs")
                return@withContext ResultResponse.Failed(Exception("Error in getting userId from prefs"))
            }
            val summarizeQuery = AIQuery(
                role = "user",
                content = "Please summarize the messages above in just 3 sentences, focusing on 'key interactions' between me and other user."
            )
            return@withContext summarizeAiChatMessages(
                messages.map { it.toAIChatMessage(role = "user", currentUserId = selfUser.userId) },
                summarizeQuery = summarizeQuery
            )
        }
    }

    override suspend fun observeAIChatMessages() = aiChatMessageDao.observeAIChatMessages()
        .map { it.map { aiChatMessageEntity -> aiChatMessageEntity.toAIChatMessage() } }

    override suspend fun deleteAIChatMessage(aiChatMessages: List<AIChatMessage>): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(tempTag(), "message $aiChatMessages")
                aiChatMessageDao.deleteAIChatMessages(aiChatMessages.map { it.toAiChatMessageEntity() })
                ResultResponse.Success(Unit)
            } catch (e: Exception) {
                ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun clearAIChatConversation(): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                aiChatMessageDao.clearAIChatConversation()
                ResultResponse.Success(Unit)
            } catch (e: Exception) {
                ResultResponse.Failed(e)
            }
        }
    }


    override suspend fun getConversationDetails(conversationId: String): ResultResponse<Conversation> =
        withContext(Dispatchers.IO) {
            val selfUser = getUser()
            if (selfUser == null) {
                Log.i(tempTag(), "Error in getting user from prefs")
                return@withContext ResultResponse.Failed(Exception("Error in getting user from prefs"))
            }
            when (val conversationRes =
                getSingleChat(conversationId).map { it.toConversation(selfUser) }) {
                is ResultResponse.Success -> {
                    if (conversationRes.data == null) {
                        return@withContext ResultResponse.Failed(Exception("Error in converting single chat to conversation"))
                    }
                    return@withContext ResultResponse.Success(conversationRes.data)
                }

                is ResultResponse.Failed -> {
                    return@withContext ResultResponse.Failed(conversationRes.exception)
                }
            }
        }

    private suspend fun summarizeAiChatMessages(
        aiChatMessages: List<AIChatMessage>,
        summarizeQuery: AIQuery
    ): ResultResponse<AIChatMessage> {
        return withContext(Dispatchers.IO) {
            val chatRequest = AIChatRequestBody(
                model = "meta-llama/Meta-Llama-3.1-8B-Instruct-lora",
                messages = aiChatMessages.map { it.toAiQuery() } + summarizeQuery
            )
            Log.i(tempTag(), "summarize $chatRequest")
            return@withContext getChatBotResponse(chatRequest)
        }
    }

    private suspend fun getChatBotResponse(chatRequestBody: AIChatRequestBody): ResultResponse<AIChatMessage> {
        return withContext(Dispatchers.IO) {
            try {
                val responseBody =
                    aiChatService.getChatBotResponse(chatRequestBody)
                if (responseBody.choices.isEmpty()) {
                    return@withContext ResultResponse.Failed(AIChatBotException.GeneralException("response body is empty."))
                }
                val createdAt = java.util.Date().time

                val aiChatMessageEntity =
                    responseBody.choices.first().message.toAIChatMessageEntity(
                        createdAt, AIChatMessageType.INCOMING.value
                    )
                return@withContext ResultResponse.Success(aiChatMessageEntity.toAIChatMessage())
            } catch (e: Exception) {
                return@withContext ResultResponse.Failed(AIChatBotException.GeneralException(e.toString()))
            }
        }
    }
}