package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.model.MessageStatus
import com.example.chattingApp.domain.model.MessageType

data class MessageDto(
    var messageId: String = "",
    var textContent: String = "",
    var senderId: String = "",
    var status: String = MessageStatus.INITIAL.name,
    var timeStamp: Long = 0,
    var conversationId: String = "",
    // var because custom property name
) {
    fun toMessage(currentUserId: String): Message {
//        val messageStatus = try {
//            MessageStatus.valueOf(status)
//        } catch (e: Exception) {
//            MessageStatus.INITIAL
//        }
        val messageStatus = MessageStatus.valueOf(status)
        return Message(
            messageId = messageId,
            textContent = textContent,
            senderId = senderId,
            status = messageStatus,
            timeStamp = timeStamp,
            conversationId = conversationId,
            type = if (senderId == currentUserId) MessageType.OUTGOING else MessageType.INCOMING
        )
    }
}




