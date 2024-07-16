package com.example.chattingApp.ui.screens.chatlistscreen

import com.example.chattingApp.domain.model.Conversation

data class ChatListScreenState (
    var conversations: List<Conversation> = emptyList(),
    var isLoading: Boolean = true,
)