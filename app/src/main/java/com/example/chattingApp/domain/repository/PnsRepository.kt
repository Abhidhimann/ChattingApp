package com.example.chattingApp.domain.repository

import com.example.chattingApp.utils.ResultResponse

interface PnsRepository {
    suspend fun sendMessagePns(
        chatRoomId: String,
        chatRoomTitle: String,
        messageId: String,
        senderId: String,
        textContent: String,
    ): ResultResponse<Unit>

    suspend fun handleReceivedPns(pns: Map<String, String>): ResultResponse<Unit>
}