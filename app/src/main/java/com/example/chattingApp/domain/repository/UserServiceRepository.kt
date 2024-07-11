package com.example.chattingApp.domain.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.data.remote.UserService
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserRelation
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.utils.tempTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    // todo later change it to db
    private var getUser: Lazy<UserSummary?> = lazy {
        val userId = appPrefs.getString("user_id", "")
        val profileImageUrl = appPrefs.getString("profile_url", "")
        val name = appPrefs.getString("user_name", "")
        if (userId == null || profileImageUrl == null || name == null) return@lazy null
        return@lazy UserSummary(name = name, profileImageUrl = profileImageUrl, userId = userId)
    }

    private fun saveUserInPref(userSummary: UserSummary) {
        appPrefs.edit().apply {
            this.putString("user_id", userSummary.userId)
            this.putString("profile_url", userSummary.profileImageUrl)
            this.putString("user_name", userSummary.name)
        }.apply()
    }

    suspend fun createUser() {
        withContext(Dispatchers.IO) {
            val result = userService.createUser()
            Log.i(tempTag(), "user created with $result}")
            if (result.isSuccess && result.getOrNull() != null) {
                Log.i(tempTag(), "Yeah user create with ${result.getOrNull()}")
                saveUserInPref(
                    UserSummary(
                        name = "",
                        userId = result.getOrNull().toString(),
                        profileImageUrl = ""
                    )
                )
            } else {
                Log.i(tempTag(), "fail user create with ${result.exceptionOrNull()}")
            }
        }
    }

    suspend fun observeNonConnectedUsers() = withContext(Dispatchers.IO) {
        val fromUser = getUser.value
        if (fromUser == null) {
            Log.i(tempTag(), "Error in getting user from prefs")
            return@withContext emptyFlow<UserProfile>()
        }
        // todo replace it with db call later
        val fromUserFriends = async { getUserFriends(fromUser.userId) }
        val fromUserOutgoingConnectRequest =
            async { getUserIncomingOutgoingConnectRequests(fromUser.userId) }
        awaitAll(fromUserFriends, fromUserOutgoingConnectRequest)
        Log.i(
            tempTag(),
            "friends are ${fromUserFriends.await()} & outgoing req ${fromUserOutgoingConnectRequest.await()}"
        )
        userService.observeNonConnectedUsers(
            fromUser.userId,
            fromUserFriends.await().map { it.userId })
            .map { userProfileDto ->
                val userProfile = userProfileDto.toUserProfile()
                // checking if user has already send a connect request before
                // can also remove will be easy
                if (fromUserOutgoingConnectRequest.await()
                        .any { it.userId == userProfile.userId }
                ) {
                    userProfile.relation = UserRelation.ALREADY_REQUESTED
                }
                userProfile
            }
    }

    suspend fun getUserProfileDtoDetails(userId: String) = withContext(Dispatchers.IO) {
        return@withContext userService.getUserProfileDetails(userId)
    }

    suspend fun getUserFriends(userId: String) = withContext(Dispatchers.IO) {
        return@withContext userService.getUserFriends(userId)
    }

    suspend fun getUserIncomingConnectRequests(userId: String) = withContext(Dispatchers.IO) {
        return@withContext userService.getUserIncomingConnectRequests(userId)
    }

    suspend fun getUserIncomingOutgoingConnectRequests(userId: String) =
        withContext(Dispatchers.IO) {
            return@withContext userService.getUserOutgoingConnectRequests(userId)
        }

    suspend fun getUserProfileDetails(userId: String) =
        getUserProfileDtoDetails(userId)?.toUserProfile()

    suspend fun getSelfProfileDetails(): UserProfile? {
        return withContext(Dispatchers.IO) {
            val fromUser = getUser.value
            if (fromUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext null
            }
            getUserProfileDtoDetails(fromUser.userId)?.toUserProfile()
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

    suspend fun sendConnectionRequest(toUserId: String): Int {
        return withContext(Dispatchers.IO) {
            val fromUser = getUser.value
            if (fromUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext -1
            }
            userService.sendConnectionRequest(
                toUserId,
                getUser.value!!.userId
            )
        }
    }

    suspend fun removeConnectionRequestByRequestedUser(toUserId: String) =
        withContext(Dispatchers.IO) {
            val fromUser = getUser.value
            if (fromUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext
            }
            userService.removeConnectRequest(
                toUserId,
                getUser.value!!.userId
            )
        }

    suspend fun removeConnectionRequestBySelf(toUserId: String) =
        withContext(Dispatchers.IO) {
            val fromUser = getUser.value
            if (fromUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext
            }
            userService.removeConnectRequest(
                getUser.value!!.userId,
                toUserId
            )
        }

    suspend fun observeIncomingRequests() = withContext(Dispatchers.IO) {
        val fromUser = getUser.value
        if (fromUser == null) {
            Log.i(tempTag(), "Error in getting userId")
            return@withContext emptyFlow<UserProfile>()
        }
        return@withContext userService.observeConnectionRequests(fromUser.userId).map {
            it.toUserProfile()
        }
    }

    suspend fun temp() {
        userService.createUser()
//        appPrefs.edit().remove("user_id").apply()
    }
}