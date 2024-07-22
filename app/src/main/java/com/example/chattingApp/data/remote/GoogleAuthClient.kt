package com.example.chattingApp.data.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.WEB_CLIENT_ID
import com.example.chattingApp.utils.classTag
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleAuthClient(
    private val credentialManager: CredentialManager,
    @ApplicationContext private val appContext: Context
) {


    suspend fun getAuthCredentialFromGoogleSso(): ResultResponse<AuthCredential> {
        return withContext(Dispatchers.IO) {
            try {
                val response = credentialManager.getCredential(
                    request = getCredentialRequest(),
                    context = appContext
                )
                return@withContext getAuthCredentialFromCredentialResponse(response)
            } catch (e: Exception) {
                Log.e(classTag(), "Error while getting credentials from google sso")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        return GetCredentialRequest.Builder().setCredentialOptions(listOf(googleIdOption)).build()
    }

    private suspend fun getAuthCredentialFromCredentialResponse(response: GetCredentialResponse): ResultResponse<AuthCredential> {
        return withContext(Dispatchers.IO) {
            when (val credential = response.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )
                            return@withContext ResultResponse.Success(authCredential)
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e(classTag(), "Received an invalid google id token response", e)
                            return@withContext ResultResponse.Failed(e)
                        }
                    } else {
                        Log.e(classTag(), "Unexpected type of credential")
                        return@withContext ResultResponse.Failed(Exception("Unexpected type of credential"))
                    }
                }

                else -> {
                    Log.e(classTag(), "Unexpected type of credential")
                    return@withContext ResultResponse.Failed(Exception("Unexpected type of credential"))
                }
            }
        }
    }
}