package com.example.chattingApp.domain.repository

import android.net.Uri
import androidx.browser.trusted.Token
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.utils.ResultResponse
import kotlinx.coroutines.flow.Flow

interface UserServiceRepository {
    suspend fun observeNonConnectedUsers(): Flow<UserProfile>
    suspend fun observeIncomingRequests(): Flow<UserSummary?>
    suspend fun getUserIncomingConnectRequests(userId: String): ResultResponse<List<UserSummary>>
    suspend fun getUserProfileDetails(userId: String): ResultResponse<UserProfile>
    suspend fun getSelfProfileDetails(): ResultResponse<UserProfile>
    suspend fun updateUserProfile(userProfile: UserProfile): ResultResponse<Unit>
    suspend fun sendConnectionRequest(toUser: UserSummary): ResultResponse<Unit>
    suspend fun removeConnectionRequestByRequestedUser(toUserId: String): ResultResponse<Unit>
    suspend fun removeConnectionRequestBySelf(toUserId: String): ResultResponse<Unit>
    suspend fun acceptConnectionRequest(fromUser: UserSummary): ResultResponse<Unit>
    suspend fun uploadUserPic(imageUri: Uri): ResultResponse<String>
    suspend fun updateCurrentChatRoom(chatId: String?): ResultResponse<Unit>
    suspend fun updateUserToken(token: String?): ResultResponse<Unit>
    suspend fun updateUserTokenFromLocal(): ResultResponse<Unit>
    suspend fun saveUserTokenLocally(token: String?)
}