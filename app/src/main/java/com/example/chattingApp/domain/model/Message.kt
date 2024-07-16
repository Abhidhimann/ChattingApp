package com.example.chattingApp.domain.model

import androidx.compose.runtime.mutableStateListOf
import com.example.chattingApp.data.remote.dto.MessageResponse

data class Message(
    var messageId: String = "",
    val textContent: String,
    val senderId: String,
    var status: MessageStatus = MessageStatus.INITIAL,
    var timeStamp: Long,
    val conversationId: String,
    val type: MessageType? = null
) {
    fun toMessageDto(): MessageResponse {
        return MessageResponse(
            messageId = messageId,
            textContent = textContent,
            senderId = senderId,
            status = status.name,
            conversationId = conversationId,
            timeStamp = timeStamp
        )
    }
}

val tempMessageList = mutableStateListOf<Message>(
    Message(
        messageId = "",
        textContent = "How are you",
        senderId = "",
        status = MessageStatus.INITIAL,
        timeStamp = 0,
        conversationId = "",
        type = MessageType.OUTGOING
    ),
    Message(
        messageId = "",
        textContent = "Good",
        senderId = "",
        status = MessageStatus.INITIAL,
        timeStamp = 0,
        conversationId = "",
        type = MessageType.INCOMING
    )
)
enum class MessageStatus {
    INITIAL,
    SENT,
    UNREAD,
    READ,
}

enum class MessageType {
    INCOMING,
    OUTGOING
}
