package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.MessageResponse
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun sendMessage(messageResponse: MessageResponse): Int
    suspend fun observeMessages(conversationId: String): Flow<MessageResponse>
}