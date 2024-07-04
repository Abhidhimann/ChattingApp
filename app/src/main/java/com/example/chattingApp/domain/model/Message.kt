package com.example.chattingApp.domain.model

import androidx.compose.runtime.mutableStateListOf

data class Message(
    val messageId: Long,
    val textContent: String,
    val senderId: Long,
    val conversationId: Long,
    val timeStamp: String
)

val messageList = mutableStateListOf<Message>(
    Message(12,"hi", 12, 12, ""),
    Message(12,"hi how are you", 12, 12, ""),
    Message(12,"I'm good lets see", 13, 12, ""),
    Message(12,"wow ", 12, 12, ""),
    Message(12,"yes", 13, 13, ""),
    Message(12,"yeahh", 12, 12, ""),
)



