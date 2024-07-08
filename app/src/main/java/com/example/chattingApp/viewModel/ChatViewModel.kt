package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.data.remote.ChatSocketService
import com.example.chattingApp.data.remote.FirebaseMessageService
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.ui.screens.chatscreen.ChatScreenEvent
import com.example.chattingApp.ui.screens.chatscreen.ChatScreenState
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatSocketService: ChatSocketService,
    private val firebaseMessageService: FirebaseMessageService
) : ViewModel() {

//    private val _state = mutableStateOf(ChatState())
//    val state: State<ChatState> = _state

    var state by mutableStateOf(ChatScreenState())
        private set

    private fun sendMessage(message: Message) {
        viewModelScope.launch {
            chatSocketService.sendMessage(message.toMessageDto())
        }
    }

     private fun observeMessages(conversationId: String) {
        viewModelScope.launch {
            Log.i(tempTag(), "calling this")
            chatSocketService.observeMessages(conversationId)
                .catch { e ->
                    Log.e(classTag(), "Error observing messages", e)
                }
                .collect { messageDto ->
                    Log.i(classTag(), "got message from flow $messageDto")
//                    Log.i(classTag(), "previous message list ${state.value.messages}")
//
//                    val newList = state.value.messages.toMutableList().apply {
//                        add(0, messageDto.toMessage())
//                    }
//                    _state.value = _state.value.copy(
//                        messages = newList
//                    )
//                    Log.i(classTag(), "new message list ${state.value.messages}")
                    state = state.copy(
                        messages = mutableListOf(messageDto.toMessage()) + state.messages // prepend new message
                    )
                    Log.i(classTag(), "new message list ${state.messages}")
                }
        }
    }

    fun onEvent(event: ChatScreenEvent){
        when(event){
            is ChatScreenEvent.SendMessage -> {
                sendMessage(event.message)
            }
            is ChatScreenEvent.ObserverMessages -> {
                observeMessages(event.conversationId)
            }
        }
    }
}