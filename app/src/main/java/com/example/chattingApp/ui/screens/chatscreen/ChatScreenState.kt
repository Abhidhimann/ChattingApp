package com.example.chattingApp.ui.screens.chatscreen

import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.Message

data class ChatScreenState(
    var messages: List<Message> = emptyList(),
    var conversationDetails: Conversation? = null,
    var isLoading: Boolean = false,
    var isChatDetailsFetchSuccess: Boolean = true
)

