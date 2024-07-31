package com.example.chattingApp.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.chattingApp.data.remote.auth.AuthService
import com.example.chattingApp.data.remote.image.ImageService
import com.example.chattingApp.data.remote.user.UserService
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.ui.util.compressImageTillSize
import com.example.chattingApp.ui.util.fileFromContentUri
import com.example.chattingApp.utils.onSuccess
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserServiceRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val imageService: ImageService,
    private val authService: AuthService,
    private val appPrefs: SharedPreferences,
    @ApplicationContext private val applicationContext: Context
) : UserServiceRepository {
    // todo later change it to db
    private fun getUser(): UserSummary? {
        val userId = appPrefs.getString("user_id", "")
        val profileImageUrl = appPrefs.getString("profile_url", "")
        val name = appPrefs.getString("user_name", "")
        if (userId == null || profileImageUrl == null || name == null) return null
        return UserSummary(name = name, profileImageUrl = profileImageUrl, userId = userId)
    }

    private fun saveUserInPref(userSummary: UserSummary) {
        appPrefs.edit().apply {
            this.putString("user_id", userSummary.userId)
            this.putString("profile_url", userSummary.profileImageUrl)
            this.putString("user_name", userSummary.name)
        }.apply()
    }


    override suspend fun observeNonConnectedUsers() = withContext(Dispatchers.IO) {
        val selfUser = getUser()
        if (selfUser == null) {
            Log.i(tempTag(), "Error in getting user from prefs")
            return@withContext emptyFlow<UserProfile>()
        }
        // todo replace it with db call later
        val userFriendsDeff = async { getUserFriends(selfUser.userId) }
        val userOutgoingConnectRequestsDeff =
            async { getUserOutgoingConnectRequests(selfUser.userId) }

        awaitAll(userFriendsDeff, userOutgoingConnectRequestsDeff)
        var userFriends = emptyList<String>()
        var userOutgoingConnectRequests = emptyList<String>()
        userFriendsDeff.await().onSuccess {
            userFriends = it.map { userSummary -> userSummary.userId }
        }

        userOutgoingConnectRequestsDeff.await().onSuccess {
            userOutgoingConnectRequests = it.map { userSummary -> userSummary.userId }
        }

        Log.i(tempTag(), "friends are $userFriends & outgoing req $userOutgoingConnectRequests")
        userService.observeNonConnectedUsers(
            selfUser.userId,
            userFriends + userOutgoingConnectRequests
        ).map { userProfileDto ->
            val userProfile = userProfileDto.toUserProfile()
            // checking if user has already send a connect request before
            // can also remove will be easy
//            if (userOutgoingConnectRequests.any { it == userProfile.userId }) {
//                userProfile.relation = UserRelation.ALREADY_REQUESTED
//            }
            userProfile
        }
    }

    private suspend fun getUserProfileDtoDetails(userId: String) = withContext(Dispatchers.IO) {
        return@withContext userService.getUserProfileDetails(userId)
    }

    private suspend fun getUserFriends(userId: String) = withContext(Dispatchers.IO) {
        return@withContext userService.getUserFriendsDetails(userId)
    }

    override suspend fun getUserIncomingConnectRequests(userId: String): ResultResponse<List<UserSummary>> =
        withContext(Dispatchers.IO) {
            return@withContext userService.getIncomingConnectRequestingUsers(userId)
                .map { it.map { it.toUserSummary() } }
        }

    private suspend fun getUserOutgoingConnectRequests(userId: String) =
        withContext(Dispatchers.IO) {
            return@withContext userService.getOutgoingConnectRequestingUsers(userId)
        }

    override suspend fun getUserProfileDetails(userId: String) =
        getUserProfileDtoDetails(userId).map { it.toUserProfile() }

    override suspend fun getSelfProfileDetails(): ResultResponse<UserProfile> {
        return withContext(Dispatchers.IO) {
            val selfUser = getUser()
            if (selfUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext ResultResponse.Failed(Exception("Error in getting userId"))
            }
            val userProfile = getUserProfileDtoDetails(selfUser.userId).map { it.toUserProfile() }
            return@withContext userProfile
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): ResultResponse<Unit> =
        withContext(Dispatchers.IO) {
            when (val result = userService.updateUserProfile(userProfile.toUserProfileDto())) {
                is ResultResponse.Success -> {
                    saveUserInPref(
                        UserSummary(
                            name = userProfile.name,
                            userId = userProfile.userId,
                            profileImageUrl = userProfile.profileImageUrl
                        )
                    )
                    return@withContext ResultResponse.Success(Unit)
                }

                is ResultResponse.Failed -> {
                    return@withContext ResultResponse.Failed(result.exception)
                }
            }
            // todo later inject local datasource and update db here
        }

    // todo will implement logic if other user previous send request to me
    // and when i send connection request to same user then i accept it instead of
    // sending request
    override suspend fun sendConnectionRequest(toUser: UserSummary): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            val fromUser = getUser()
                ?: return@withContext ResultResponse.Failed(Exception("Error in getting userId"))
            return@withContext userService.sendConnectionRequest(
                toUser.toUserSummaryDto(),
                fromUser.toUserSummaryDto()
            )
        }
    }

    override suspend fun removeConnectionRequestByRequestedUser(toUserId: String) =
        withContext(Dispatchers.IO) {
            val fromUser = getUser()
            if (fromUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext ResultResponse.Failed(Exception("Error in getting userId"))
            }
            return@withContext userService.removeConnectRequest(
                toUserId,
                fromUser.userId
            )
        }

    override suspend fun removeConnectionRequestBySelf(toUserId: String) =
        withContext(Dispatchers.IO) {
            val fromUser = getUser()
            if (fromUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext ResultResponse.Failed(Exception("Error in getting userId"))
            }
            userService.removeConnectRequest(
                fromUser.userId,
                toUserId
            )
        }

    override suspend fun observeIncomingRequests() = withContext(Dispatchers.IO) {
        val fromUser = getUser()
        if (fromUser == null) {
            Log.i(tempTag(), "Error in getting userId")
            return@withContext emptyFlow<UserSummary>()
        }
        return@withContext userService.observeConnectionRequests(fromUser.userId).map {
            it?.toUserSummary()
        }
    }

    override suspend fun acceptConnectionRequest(fromUser: UserSummary): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            val toUser = getUser()
            if (toUser == null) {
                Log.i(tempTag(), "Error in getting userId")
                return@withContext ResultResponse.Failed(Exception("Error in getting userId"))
            }
            return@withContext userService.acceptConnectRequestAndCreateChat(
                toUser.toUserSummaryDto(),
                fromUser.toUserSummaryDto()
            )
        }
    }

    override suspend fun uploadUserPic(imageUri: Uri): ResultResponse<String> {
        val fromUser =
            getUser() ?: return ResultResponse.Failed(Exception("Error in getting userId"))

        return withContext(Dispatchers.IO) {
            val imageFile = fileFromContentUri(applicationContext, imageUri)
            val compressedImageFile = compressImageTillSize(applicationContext, imageFile, 0.6)
            Log.i(classTag(), "file size is ${compressedImageFile.length()}")
            val resultUri = Uri.fromFile(compressedImageFile)
            if (resultUri == null) {
                Log.i(classTag(), "unable to compress image uri is null")
                return@withContext ResultResponse.Failed(Exception("unable to compress image uri is null"))
            }
            // user all files we will keep at userPictures/userId folder
            return@withContext imageService.uploadImageToFirebaseStorage(
                "userPictures/${fromUser.userId}/${fromUser.userId}",
                resultUri
            )
        }
    }

    override suspend fun logOut(): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            authService.logOut()
        }
    }
}