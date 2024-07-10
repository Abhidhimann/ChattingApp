package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.UserProfileDto
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun createUser(): Result<Any>
    suspend fun updateUserProfile(userId: String, userProfile: UserProfileDto): Int
    suspend fun findRandomUser(): String
    suspend fun updateUserOnlineStatus(userId: String, value: Boolean): Int
    suspend fun sendConnectionRequest(toUserId: String, fromUserId: String): Int
    suspend fun removeConnectRequest(toUserId: String, fromUserId: String): Int
    suspend fun observeNonConnectedUsers(fromUserId: String, friendsIdList: List<String>): Flow<UserProfileDto>
    suspend fun getUserProfileDetails(fromUserId: String): UserProfileDto?
}