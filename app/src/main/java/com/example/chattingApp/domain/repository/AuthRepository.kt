package com.example.chattingApp.domain.repository

import com.example.chattingApp.utils.ResultResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUpUsingEmailAndPassword(email: String, password: String, name: String): ResultResponse<Unit>
    suspend fun signInUsingEmailAndPassword(email: String, password: String): ResultResponse<Unit>
    suspend fun getAuthState(): Flow<Boolean>
    suspend fun signInWithGoogleSso(): ResultResponse<Unit>
    suspend fun sendPasswordResetEmail(email: String): ResultResponse<Unit>
    suspend fun logOut(): ResultResponse<Unit>
}