package com.example.chattingApp.domain.repository

import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.utils.ResultResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getConversationDetails(conversationId: String): ResultResponse<Conversation>
    suspend fun observeMessages(conversationId: String): Flow<Message>
    suspend fun sendMessage(message: Message): ResultResponse<String>
    suspend fun getChatBotResponse(aiChatMessages: List<AIChatMessage>): ResultResponse<AIChatMessage>
}