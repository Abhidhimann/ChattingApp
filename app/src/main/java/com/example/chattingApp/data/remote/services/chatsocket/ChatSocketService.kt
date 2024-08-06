package com.example.chattingApp.data.remote.services.chatsocket

import com.example.chattingApp.data.remote.dto.MessageResponse
import com.example.chattingApp.utils.ResultResponse
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun sendMessage(messageResponse: MessageResponse): ResultResponse<String>
    suspend fun observeMessages(conversationId: String): Flow<MessageResponse>
}