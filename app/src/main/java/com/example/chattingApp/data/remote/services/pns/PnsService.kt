package com.example.chattingApp.data.remote.services.pns

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface PnsService {
    @POST("/sendNotification")
    suspend fun sendMessagePns(
        @Body request: NotificationRequest
    ): Response<String>
}

data class NotificationRequest(
    @SerializedName("chatRoomId")
    val chatRoomId: String,
    @SerializedName("messageId")
    val messageId: String,
    @SerializedName("senderId")
    val senderId: String,
)