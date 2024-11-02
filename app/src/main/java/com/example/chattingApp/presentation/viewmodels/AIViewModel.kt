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
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    var state by mutableStateOf(AIChatBotScreenState())
        private set

    private fun getChatBotResponse(aiChatMessage: AIChatMessage) = viewModelScope.launch {
        when (val result = chatRepository.getChatBotResponse(aiChatMessage)) {
            is ResultResponse.Success -> {
                Log.i(tempTag(), "Got response from chat bot ${result.data}")
                val aiMessage = result.data
                val aiChatMessages = state.aiChatMessages.toMutableList()
                aiChatMessages.add(0, aiMessage)
                state = state.copy(
                    aiChatMessages = aiChatMessages
                )
            }

            is ResultResponse.Failed -> {
                Log.i(tempTag(), "Chat bot failed with ${result.exception}")
            }
        }
    }

    private fun generateAiChatMessage(text: String): AIChatMessage {
        return AIChatMessage(
            role = "user",
            content = text,
            createdAt = java.util.Date().time,
            type = AIChatMessageType.OUTGOING
        )
    }

    fun onEvent(event: AIChatBotScreenEvent) {
        when (event) {
            is AIChatBotScreenEvent.SendQuery -> {
                val aiMessage = generateAiChatMessage(event.text)
                val aiChatMessages = state.aiChatMessages.toMutableList()
                aiChatMessages.add(0, aiMessage)
                state = state.copy(
                    aiChatMessages = aiChatMessages
                )
                getChatBotResponse(aiMessage)
            }

            else -> Unit
        }
    }
}