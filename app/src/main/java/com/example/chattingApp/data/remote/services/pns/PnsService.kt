package com.example.chattingApp.data.remote.services.pns

import com.example.chattingApp.data.remote.dto.MessageNotification
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface PnsService {
    @POST("/sendNotification")
    suspend fun sendMessagePns(
        @Body request: MessageNotification
    ): Response<String>
}