package com.example.chattingApp.data.remote

import com.example.chattingApp.utils.ResultResponse
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthService {
    suspend fun getAuthState(): Flow<FirebaseUser?>
    suspend fun logOut(): ResultResponse<Unit>
    suspend fun signInByEmailAndPassword(email: String, password: String): ResultResponse<FirebaseUser>
    suspend fun signUpWithEmailPassword(email: String, password: String): ResultResponse<FirebaseUser>
    suspend fun signInWithGoogleSso(): ResultResponse<FirebaseUser>

    suspend fun signInWithAuthCredentials(authCredential: AuthCredential): ResultResponse<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): ResultResponse<Unit>
}