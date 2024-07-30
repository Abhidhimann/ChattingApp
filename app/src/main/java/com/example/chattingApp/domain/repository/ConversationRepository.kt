package com.example.chattingApp.domain.repository

import com.example.chattingApp.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun observerConversations(): Flow<Conversation?>
}