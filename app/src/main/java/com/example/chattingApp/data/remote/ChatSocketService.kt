package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.MessageDto
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun sendMessage(messageDto: MessageDto): Int
    suspend fun observeMessages(conversationId: String): Flow<MessageDto>
}