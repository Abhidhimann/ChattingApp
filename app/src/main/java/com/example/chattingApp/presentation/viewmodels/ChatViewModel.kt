package com.example.chattingApp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.repository.ChatRepository
import com.example.chattingApp.domain.repository.PnsRepository
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.presentation.ui.screens.chatscreen.ChatScreenEvent
import com.example.chattingApp.presentation.ui.screens.chatscreen.ChatScreenState
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
    private val chatRepository: ChatRepository,
    private val userServiceRepository: UserServiceRepository,
    private val pnsRepository: PnsRepository
) : ViewModel() {

    var state by mutableStateOf(ChatScreenState())
        private set

    private var conversation: Conversation? = null

    private fun sendMessage(message: Message) {
        viewModelScope.launch {
            when (val result = chatRepository.sendMessage(message)) {
                is ResultResponse.Success -> {
                    Log.i(classTag(), "message send successfully with id ${result.data}")
                    pnsRepository.sendMessagePns(
                        message.conversationId,
                        conversation!!.title,
                        result.data,
                        message.senderId,
                        message.textContent
                    )
                }

                is ResultResponse.Failed -> {
                    // mark message as failed in that condition todo
                }
            }
        }
    }

    // todo kind of bad ( don't know) will think about later
    private fun updateCurrentChatRoom(chatId: String?) = viewModelScope.launch {
        userServiceRepository.updateCurrentChatRoom(chatId)
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
                    state = state.copy(conversationDetails = conversation, isChatDetailsFetchSuccess = true)
                    updateCurrentChatRoom(conversationId)
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

            is ChatScreenEvent.UpdateCurrentChatRoom -> {
                updateCurrentChatRoom(event.chatId)
            }

            else -> Unit
        }
    }
}