package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.Message
import com.google.firebase.firestore.PropertyName


data class MessageDto(
    @get:PropertyName("message_id")
    @set:PropertyName("message_id")
    var messageId: Long = 0,
    @get:PropertyName("text_content")
    @set:PropertyName("text_content")
    var textContent: String = "",
    @get:PropertyName("sender_id")
    @set:PropertyName("sender_id")
    var senderId: Long = 0,
    @get:PropertyName("conversation_id")
    @set:PropertyName("conversation_id")
    var conversationId: String = "",
    @get:PropertyName("time_stamp")
    @set:PropertyName("time_stamp")
    var timeStamp: String = ""
    // var because custom property name
) {
    fun toMessage(): Message {
        return Message(this.messageId, this.textContent, this.senderId, this.conversationId, this.timeStamp)
    }
}




