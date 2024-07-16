package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import kotlinx.coroutines.flow.Flow

interface SingleChatService {
    suspend fun createSingleChat(originator: UserSummaryResponse, recipient: UserSummaryResponse): Int
    suspend fun updateUserSummaryInChat(chatId: String, userSummary: UserSummaryResponse): Int
    suspend fun getSingleChat(chatId: String): SingleChatResponse?
    suspend fun observeSingleChats(userId: String): Flow<SingleChatResponse>

    // because after accepting request chat should create so will make it a transaction
    suspend fun acceptConnectRequestAndCreateChat(toUser: UserSummaryResponse, fromUser: UserSummaryResponse): Int
}