package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.utils.ResultResponse
import kotlinx.coroutines.flow.Flow

interface SingleChatService {
    suspend fun createSingleChat(originator: UserSummaryResponse, recipient: UserSummaryResponse): ResultResponse<Unit>
    suspend fun updateUserSummaryInChat(singleChatDto : SingleChatResponse, userSummary: UserSummaryResponse): ResultResponse<Unit>
    suspend fun getSingleChat(chatId: String): ResultResponse<SingleChatResponse>
    suspend fun observeSingleChats(userId: String): Flow<SingleChatResponse>

    // because after accepting request chat should create so will make it a transaction
    suspend fun acceptConnectRequestAndCreateChat(toUser: UserSummaryResponse, fromUser: UserSummaryResponse): ResultResponse<Unit>
}