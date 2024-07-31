package com.example.chattingApp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.repository.ChatRepository
import com.example.chattingApp.ui.screens.chatscreen.ChatScreenEvent
import com.example.chattingApp.ui.screens.chatscreen.ChatScreenState
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
//    private val chatSocketService: ChatSocketService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    var state by mutableStateOf(ChatScreenState())
        private set

    private var conversation: Conversation? = null

    private fun sendMessage(message: Message) {
        viewModelScope.launch {
            when (val result = chatRepository.sendMessage(message)) {
                is ResultResponse.Success -> Unit

                is ResultResponse.Failed -> {
                    // mark message as failed in that condition todo
                }
            }
        }
    }

    private fun observeMessages(conversationId: String) {
        viewModelScope.launch {
            Log.i(tempTag(), "calling this")
            chatRepository.observeMessages(conversationId)
                .catch { e ->
                    Log.e(classTag(), "Error observing messages", e)
                }
                .collect { message ->
                    Log.i(classTag(), "got message from flow $message")
                    val updatedMessages = state.messages.toMutableList()
                    val existingMessageIndex =
                        updatedMessages.indexOfFirst { it.messageId == message.messageId }

                    if (existingMessageIndex != -1) {
                        updatedMessages[existingMessageIndex]
                    } else {
                        updatedMessages.add(0, message)
                    }
                    state = state.copy(messages = updatedMessages) // prepend new message
                    Log.i(classTag(), "new message list ${state.messages}")
                }
        }
    }

    private fun generateMessage(textContent: String): Message {
        return Message(
            textContent = textContent,
            senderId = conversation!!.currentUserId,
            timeStamp = System.currentTimeMillis(),
            conversationId = conversation!!.conversationId,
        )
    }

    private fun getConversationDetails(conversationId: String) {
        viewModelScope.launch {
            when (val conversationResult = chatRepository.getConversationDetails(conversationId)) {
                is ResultResponse.Success -> {
                    conversation = conversationResult.data
                }

                is ResultResponse.Failed -> {
                    state = state.copy(isChatDetailsFetchSuccess = false)
                }
            }
        }
    }

    fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.SendMessage -> {
                val message = generateMessage(event.textContent)
                sendMessage(message)
            }

            is ChatScreenEvent.ObserverMessages -> {
                observeMessages(event.conversationId)
            }

            is ChatScreenEvent.GetConversationDetails -> {
                // later replace it by db call
                getConversationDetails(event.conversationId)
            }

            else -> Unit
        }
    }
}