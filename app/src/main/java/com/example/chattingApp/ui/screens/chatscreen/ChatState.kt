package com.example.chattingApp.ui.screens.chatscreen

import com.example.chattingApp.domain.model.Message

data class ChatState(
    var messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)

