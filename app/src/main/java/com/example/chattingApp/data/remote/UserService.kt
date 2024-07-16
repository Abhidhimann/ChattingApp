package com.example.chattingApp.data.remote

import com.example.chattingApp.data.remote.dto.UserProfileResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun createUser(): Result<Any>
    suspend fun getUserProfileDocumentReference(userId: String): DocumentReference?
    suspend fun updateUserProfile(userId: String, userProfile: UserProfileResponse): Int
    suspend fun updateUserOnlineStatus(userId: String, value: Boolean): Int
    suspend fun sendConnectionRequest(toUserId: String, fromUserId: String): Int
    suspend fun removeConnectRequest(toUserId: String, fromUserId: String): Int
    suspend fun observeNonConnectedUsers(fromUserId: String, friendsIdList: List<String>): Flow<UserProfileResponse>
    suspend fun getUserProfileDetails(userId: String): UserProfileResponse?
    suspend fun getUserFriends(userId: String): List<UserProfileResponse>
    suspend fun getUserIncomingConnectRequests(userId: String): List<UserProfileResponse>
    suspend fun getUserOutgoingConnectRequests(userId: String): List<UserProfileResponse>
    suspend fun observeConnectionRequests(userId: String): Flow<UserProfileResponse>
    suspend fun acceptConnectRequestAndCreateChat(toUser: UserSummaryResponse, fromUser: UserSummaryResponse): Int
}