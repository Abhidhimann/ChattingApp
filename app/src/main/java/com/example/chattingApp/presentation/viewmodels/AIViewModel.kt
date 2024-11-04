package com.example.chattingApp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.AIChatMessageType
import com.example.chattingApp.domain.repository.ChatRepository
import com.example.chattingApp.presentation.ui.screens.aichatbot.AIChatBotScreenEvent
import com.example.chattingApp.presentation.ui.screens.aichatbot.AIChatBotScreenState
import com.example.chattingApp.utils.AIChatBotException
import com.example.chattingApp.utils.AI_CHAT_QUERIES_LIMIT
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    var state by mutableStateOf(AIChatBotScreenState())
        private set

    // need to give chatBot history for better answers
    private fun getChatBotResponse(aiChatMessages: List<AIChatMessage>) = viewModelScope.launch {
        state = state.copy(isLoading = true)
        when (val result = chatRepository.getChatBotResponse(aiChatMessages.reversed())) {
            is ResultResponse.Success -> {
                state = state.copy(
                    isLoading = false
                )
            }

            is ResultResponse.Failed -> {
                Log.i(tempTag(), "Chat bot failed with ${result.exception}")
                when (result.exception) {
                    is AIChatBotException.LimitExceedException -> {
                        state = state.copy(
                            isLimitExceed = true,
                            isLoading = false
                        )
                    }

                    is AIChatBotException.GeneralException -> {
                        state = state.copy(
                            isSomeError = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun observeAIChatMessages() = viewModelScope.launch {
        chatRepository.observeAIChatMessages()
            .catch { e ->
                Log.e(classTag(), "Error observing messages", e)
            }.collect { aiChatMessages ->
                state = state.copy(aiChatMessages = aiChatMessages)

            }
    }

    private fun deleteConversation(aiChatMessages: List<AIChatMessage>) = viewModelScope.launch {
        state = state.copy(isLoading = true)
        when (val result = chatRepository.deleteAIChatMessage(aiChatMessages)) {
            is ResultResponse.Success -> {
                state = state.copy(
                    isLoading = false
                )
            }

            is ResultResponse.Failed -> {
                Log.e(classTag(), "Error in deleting messages: ${result.exception}")
                state = state.copy(
                    isSomeError = true,
                    isLoading = false
                )
            }
        }
    }

    private fun generateAiChatMessage(text: String): AIChatMessage {
        return AIChatMessage(
            role = "user",
            content = text,
            createdAt = java.util.Date().time,
            type = AIChatMessageType.OUTGOING,
            id = 0
        )
    }

    fun onEvent(event: AIChatBotScreenEvent) {
        when (event) {
            is AIChatBotScreenEvent.SendQuery -> {
                val aiMessage = generateAiChatMessage(event.text)
                val aiChatMessages = state.aiChatMessages.toMutableList()
                aiChatMessages.add(0, aiMessage)
                if (aiChatMessages.size > AI_CHAT_QUERIES_LIMIT) {
                    state = state.copy(
                        isLimitExceed = true,
                    )
                } else {
                    getChatBotResponse(aiChatMessages)
                }
            }

            is AIChatBotScreenEvent.ObserverAIChatMessages -> {
                observeAIChatMessages()
            }

            is AIChatBotScreenEvent.DeleteAIChatConversation -> {
                deleteConversation(state.aiChatMessages)
            }

            else -> Unit
        }
    }
}