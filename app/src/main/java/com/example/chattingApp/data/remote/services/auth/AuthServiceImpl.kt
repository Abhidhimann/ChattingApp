package com.example.chattingApp.data.remote.services.auth

import android.util.Log
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.SignInException
import com.example.chattingApp.utils.SignInException.EmailNotVerifiedException
import com.example.chattingApp.utils.SignUpException
import com.example.chattingApp.utils.tempTag
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleAuthClient: GoogleAuthClient
) : AuthService {
    override suspend fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser?.isEmailVerified == true) {
                trySend(firebaseAuth.currentUser).isSuccess
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun logOut(): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                auth.signOut()
                ResultResponse.Success(Unit)
            } catch (e: Exception) {
                ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun signInByEmailAndPassword(
        email: String,
        password: String
    ): ResultResponse<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(tempTag(), "Coming here with $email $password")
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                if (authResult.user != null && authResult.user!!.isEmailVerified) {
                    return@withContext ResultResponse.Success(authResult.user!!)
                } else {
                    Log.i(tempTag(), "sending verification email")
                    authResult.user?.sendEmailVerification()
                    auth.signOut()
                    return@withContext ResultResponse.Failed(EmailNotVerifiedException())
                }
            } catch (e: Exception) {
                return@withContext ResultResponse.Failed(SignInException.GeneralException("Sign in failed with $e"))
            }
        }
    }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): ResultResponse<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                authResult.user!!.sendEmailVerification().await()
                ResultResponse.Success(authResult.user!!)
            } catch (e: FirebaseAuthUserCollisionException) {
                ResultResponse.Failed(SignUpException.UserAlreadyExists())
            } catch (e: Exception) {
                ResultResponse.Failed(SignUpException.GeneralException("$e"))
            }
        }
    }

    override suspend fun signInWithGoogleSso(): ResultResponse<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            when (val authCredentialResult = googleAuthClient.getAuthCredentialFromGoogleSso()) {
                is ResultResponse.Success -> {
                    return@withContext signInWithAuthCredentials(authCredentialResult.data)
                }

                is ResultResponse.Failed -> {
                    return@withContext ResultResponse.Failed(authCredentialResult.exception)
                }
            }
        }
    }

    override suspend fun signInWithAuthCredentials(authCredential: AuthCredential): ResultResponse<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val authResult = auth.signInWithCredential(authCredential).await()
//                authResult.user!!.sendEmailVerification().await()
                ResultResponse.Success(authResult.user!!)
            } catch (e: Exception) {
                ResultResponse.Failed(SignUpException.GeneralException("$e"))
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): ResultResponse<Unit> {
        return withContext(Dispatchers.IO)
        {
            try {
                auth.sendPasswordResetEmail(email).await()
                ResultResponse.Success(Unit)
            } catch (e: Exception) {
                ResultResponse.Failed(e)
            }
        }
    }
}