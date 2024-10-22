package com.example.chattingApp.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.data.remote.services.singlechat.SingleChatService
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.services.chatsocket.ChatSocketService
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.domain.repository.ChatRepository
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
}