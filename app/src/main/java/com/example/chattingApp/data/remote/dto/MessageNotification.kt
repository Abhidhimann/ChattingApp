package com.example.chattingApp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MessageNotification(
    @SerializedName("chatRoomId")
    val chatRoomId: String,
    @SerializedName("chatRoomTitle")
    val chatRoomTitle: String,
    @SerializedName("messageId")
    val messageId: String,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("textContent")
    val textContent: String
)