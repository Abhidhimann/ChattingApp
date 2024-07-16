package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.UserProfileDto
import com.example.chattingApp.data.remote.dto.UserSummaryDto
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserSummary
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun createUser(): Result<Any>
    suspend fun getUserProfileDocumentReference(userId: String): DocumentReference?
    suspend fun updateUserProfile(userId: String, userProfile: UserProfileDto): Int
    suspend fun updateUserOnlineStatus(userId: String, value: Boolean): Int
    suspend fun sendConnectionRequest(toUserId: String, fromUserId: String): Int
    suspend fun removeConnectRequest(toUserId: String, fromUserId: String): Int
    suspend fun observeNonConnectedUsers(fromUserId: String, friendsIdList: List<String>): Flow<UserProfileDto>
    suspend fun getUserProfileDetails(userId: String): UserProfileDto?
    suspend fun getUserFriends(userId: String): List<UserProfileDto>
    suspend fun getUserIncomingConnectRequests(userId: String): List<UserProfileDto>
    suspend fun getUserOutgoingConnectRequests(userId: String): List<UserProfileDto>
    suspend fun observeConnectionRequests(userId: String): Flow<UserProfileDto>
    suspend fun acceptConnectRequestAndCreateChat(toUser: UserSummaryDto, fromUser: UserSummaryDto): Int
}