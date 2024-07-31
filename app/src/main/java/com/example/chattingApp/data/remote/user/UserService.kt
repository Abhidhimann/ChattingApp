package com.example.chattingApp.data.remote.user

import com.example.chattingApp.data.remote.dto.UserProfileResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.utils.ResultResponse
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun createUser(userDto: UserProfileResponse): ResultResponse<UserProfileResponse>
    suspend fun isUserExists(userId: String): Boolean
    suspend fun updateUserProfile(userProfile: UserProfileResponse): ResultResponse<Unit>
    suspend fun sendConnectionRequest(toUserSummary: UserSummaryResponse, fromUserSummary: UserSummaryResponse): ResultResponse<Unit>
    suspend fun removeConnectRequest(toUserId: String, fromUserId: String): ResultResponse<Unit>
    suspend fun observeNonConnectedUsers(fromUserId: String, friendsIdList: List<String>): Flow<UserProfileResponse>
    suspend fun getUserProfileDetails(userId: String): ResultResponse<UserProfileResponse>
    suspend fun getUserFriendsDetails(userId: String): ResultResponse<List<UserSummaryResponse>>
    suspend fun getIncomingConnectRequestingUsers(userId: String): ResultResponse<List<UserSummaryResponse>>
    suspend fun getOutgoingConnectRequestingUsers(userId: String): ResultResponse<List<UserSummaryResponse>>
    suspend fun observeConnectionRequests(userId: String): Flow<UserSummaryResponse?>
    suspend fun acceptConnectRequestAndCreateChat(toUser: UserSummaryResponse, fromUser: UserSummaryResponse): ResultResponse<Unit>
}