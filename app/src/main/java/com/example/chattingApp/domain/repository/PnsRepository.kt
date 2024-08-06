package com.example.chattingApp.domain.repository

import com.example.chattingApp.utils.ResultResponse
import retrofit2.http.Query

interface PnsRepository {
    suspend fun sendMessagePns(
        chatRoomId: String,
        messageId: String,
        senderId: String
    ): ResultResponse<Unit>
}