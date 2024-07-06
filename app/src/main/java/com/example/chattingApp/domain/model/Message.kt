package com.example.chattingApp.domain.model

import androidx.compose.runtime.mutableStateListOf
import com.example.chattingApp.data.remote.dto.MessageDto

data class Message(
    val messageId: Long,
    val textContent: String,
    val senderId: Long,
    val conversationId: String,
    val timeStamp: String
) {
    fun toMessageDto(): MessageDto {
        return MessageDto( this.messageId, this.textContent, this.senderId, this.conversationId, this.timeStamp)
    }
}

val messageList = mutableStateListOf<Message>(
    Message(12,"hi", 12, "", ""),
    Message(12,"hi how are you", 12, "", ""),
    Message(12,"I'm good lets see", 13, "", ""),
    Message(12,"wow ", 12, "", ""),
    Message(12,"yes", 13, "", ""),
    Message(12,"yeahh", 12, "", ""),
    Message(12,"hi", 12, "", ""),
    Message(12,"hi how are you", 12, "", ""),
    Message(12,"I'm good lets see", 13, "", ""),
    Message(12,"wow ", 12, "", ""),
    Message(12,"yes", 13, "", ""),
    Message(12,"yeahh", 12, "", ""),
    Message(12,"hi", 12, "", ""),
    Message(12,"hi how are you", 12, "", ""),
    Message(12,"I'm good lets see", 13, "", ""),
    Message(12,"wow ", 12, "", ""),
    Message(12,"yes", 13, "", ""),
    Message(12,"yeahh", 12, "", ""),
)