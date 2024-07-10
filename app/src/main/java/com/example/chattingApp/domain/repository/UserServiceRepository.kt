package com.example.chattingApp.domain.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.data.remote.UserService
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserRelation
import com.example.chattingApp.utils.tempTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserServiceRepository @Inject constructor(
    private val userService: UserService,
    private val appPrefs: SharedPreferences
) {

    suspend fun createUser() {
        withContext(Dispatchers.IO) {
            val result = userService.createUser()
            Log.i(tempTag(), "user created with $result}")
            if (result.isSuccess && result.getOrNull() != null) {
                Log.i(tempTag(), "Yeah user create with ${result.getOrNull()}")
                appPrefs.edit().putString("user_id", result.getOrNull().toString()).apply()
            } else {
                Log.i(tempTag(), "fail user create with ${result.exceptionOrNull()}")
            }
        }
    }

    suspend fun observeNonConnectedUsers() = withContext(Dispatchers.IO) {
        val fromUserId = appPrefs.getString("user_id", "")
        if (fromUserId.isNullOrEmpty()) {
            Log.i(tempTag(), "Error in getting userId")
            return@withContext emptyFlow<UserProfile>()
        }
        // todo replace it with db call later
        val user = getUserProfileDtoDetails(fromUserId)
        if (user == null) {
            Log.i(tempTag(), "Error in getting user from firebase")
            return@withContext emptyFlow<UserProfile>()
        }
        userService.observeNonConnectedUsers(fromUserId, user.friends).map { userProfileDto ->
            val userProfile = userProfileDto.toUserProfile()
            if (userProfile.requests.contains(fromUserId)) {
                userProfile.relation = UserRelation.ALREADY_REQUESTED
            }
            userProfile
        }
    }

    suspend fun getUserProfileDtoDetails(userId: String) = withContext(Dispatchers.IO) {
        return@withContext userService.getUserProfileDetails(userId)
    }

    suspend fun getUserProfileDetails(userId: String) =
        getUserProfileDtoDetails(userId)?.toUserProfile()

    suspend fun getSelfProfileDetails(): UserProfile? {
        return withContext(Dispatchers.IO) {
            val fromUserId = appPrefs.getString("user_id", "")
            // todo later replace it with lazy val after userId created on login time
            if (fromUserId.isNullOrEmpty()) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext null
            }
            getUserProfileDtoDetails(fromUserId)?.toUserProfile()
        }
    }

    suspend fun updateUserProfile(userId: String, userProfile: UserProfile) =
        withContext(Dispatchers.IO) {
            if (userService.updateUserProfile(userId, userProfile.toUserProfileDto()) == 1) {
                appPrefs.edit().putBoolean("user_profile", true).apply()
            }
            // todo later inject local datasource and update db here
        }

    suspend fun updateUserOnlineStatus(value: Boolean) =
        withContext(Dispatchers.IO) {
            val userId = appPrefs.getString("user_id", "")
            if (userId.isNullOrEmpty()) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext
            }
            userService.updateUserOnlineStatus(userId, value)
        }

    suspend fun isUserIdExists(): Boolean {
        return withContext(Dispatchers.IO) { appPrefs.contains("user_id") }
    }

    suspend fun isUserProfileExists(): Boolean {
        return withContext(Dispatchers.IO) { appPrefs.contains("user_profile") }
    }

    suspend fun sendConnectionRequest(toUserId: String) =
        withContext(Dispatchers.IO) {
            val fromUserId = appPrefs.getString("user_id", "")
            // todo later replace it with lazy val after userId created on login time
            if (fromUserId.isNullOrEmpty()) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext
            }
            userService.sendConnectionRequest(toUserId, fromUserId)
        }

    suspend fun temp() {
        userService.createUser()
//        appPrefs.edit().remove("user_id").apply()
    }
}