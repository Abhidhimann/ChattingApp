package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.SingleChatDto
import com.example.chattingApp.data.remote.dto.UserSummaryDto
import kotlinx.coroutines.flow.Flow

interface SingleChatService {
    suspend fun createSingleChat(originator: UserSummaryDto, recipient: UserSummaryDto): Int
    suspend fun updateUserSummaryInChat(chatId: String, userSummary: UserSummaryDto): Int
    suspend fun getSingleChat(chatId: String): SingleChatDto?
    suspend fun observeSingleChats(userId: String): Flow<SingleChatDto>

    // because after accepting request chat should create so will make it a transaction
    suspend fun acceptConnectRequestAndCreateChat(toUser: UserSummaryDto, fromUser: UserSummaryDto): Int
}