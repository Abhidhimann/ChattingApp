package com.example.chattingApp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.ConversationRepository
import com.example.chattingApp.presentation.ui.screens.chatlistscreen.ChatListScreenEvent
import com.example.chattingApp.presentation.ui.screens.chatlistscreen.ChatListScreenState
import com.example.chattingApp.utils.classTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ConversationRepository
) : ViewModel() {

    var state by mutableStateOf(ChatListScreenState())
        private set

    fun onEvent(event: ChatListScreenEvent) {
        when (event) {
            is ChatListScreenEvent.ObserveConversations -> {
                observerConversations()
            }

            else -> {

            }
        }
    }

    private fun observerConversations() {
        viewModelScope.launch {
            // todo later replace it by pns frm network and db observation
            repository.observerConversations()
                .catch { e ->
                    Log.e(classTag(), "Error observing conversation", e)
                }
                .collect { conversation ->
                    if (conversation == null) {
                        return@collect
                    }
                    Log.i(classTag(), "got conversation from flow $conversation")
                    val updatedConversations = state.conversations.toMutableList()
                    val existingConversationIndex =
                        updatedConversations.indexOfFirst { it.conversationId == conversation.conversationId }

                    if (existingConversationIndex != -1) {
                        updatedConversations[existingConversationIndex] = conversation
                    } else {
                        updatedConversations.add(conversation)
                    }
                    state = state.copy(conversations = updatedConversations)
                }
        }
    }

}