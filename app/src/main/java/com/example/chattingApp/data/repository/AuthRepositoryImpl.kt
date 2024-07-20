package com.example.chattingApp.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.data.remote.AuthService
import com.example.chattingApp.data.remote.UserService
import com.example.chattingApp.data.remote.dto.UserProfileResponse
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.onFailure
import com.example.chattingApp.utils.onSuccess
import com.example.chattingApp.utils.tempTag
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userService: UserService,
    private val appPrefs: SharedPreferences
) : AuthRepository {

    override suspend fun signUpUsingEmailAndPassword(
        email: String,
        password: String,
        name: String
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            when (val result = authService.signUpWithEmailPassword(email, password)) {
                is ResultResponse.Success -> {
                    when (val result2 =
                        userService.createUser(
                            UserProfileResponse(
                                userId = result.data.uid,
                                name = name,
                                email = email
                            )
                        )) {
                        is ResultResponse.Success -> return@withContext ResultResponse.Success(Unit)
                        is ResultResponse.Failed -> return@withContext ResultResponse.Failed(result2.exception)
                    }
                }

                is ResultResponse.Failed -> return@withContext ResultResponse.Failed(result.exception)
            }
        }
    }

    // todo after sign in store new user id, name in pref
    override suspend fun signInUsingEmailAndPassword(
        email: String,
        password: String
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            when (val result = authService.signInByEmailAndPassword(email, password)) {
                is ResultResponse.Success -> {
                    val userProfile = userService.getUserProfileDetails(result.data.uid)
                    Log.i(tempTag(), "user profile is $userProfile")
                    if (userProfile != null) {
                        saveUserInPref(
                            UserSummary(
                                name = userProfile.name,
                                userId = userProfile.userId,
                                profileImageUrl = userProfile.profileImageUrl
                            )
                        )
                        return@withContext ResultResponse.Success(Unit)
                    } else {
                        authService.logOut()
                        return@withContext ResultResponse.Failed(Exception("Error in getting user details but login was success"))
                    }
                }

                is ResultResponse.Failed -> {
                    return@withContext ResultResponse.Failed(result.exception)
                }
            }
        }
    }

    override suspend fun getAuthState(): Flow<Boolean> =
        withContext(Dispatchers.IO) { authService.getAuthState().map { it != null } }

    override suspend fun sendPasswordResetEmail(email: String): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            authService.sendPasswordResetEmail(email)
        }
    }

    private fun saveUserInPref(userSummary: UserSummary) {
        appPrefs.edit().apply {
            this.putString("user_id", userSummary.userId)
            this.putString("profile_url", userSummary.profileImageUrl)
            this.putString("user_name", userSummary.name)
        }.apply()
    }
}