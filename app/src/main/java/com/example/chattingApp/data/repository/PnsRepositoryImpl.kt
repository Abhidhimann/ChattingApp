package com.example.chattingApp.data.repository

import android.util.Log
import com.example.chattingApp.data.remote.services.pns.NotificationRequest
import com.example.chattingApp.data.remote.services.pns.PnsService
import com.example.chattingApp.domain.repository.PnsRepository
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PnsRepositoryImpl @Inject constructor(private val pnsService: PnsService) : PnsRepository {
    override suspend fun sendMessagePns(
        chatRoomId: String,
        messageId: String,
        senderId: String
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    pnsService.sendMessagePns(NotificationRequest(chatRoomId, messageId, senderId))
                Log.i(classTag(), "response is ${response.body()}")
                if (!response.isSuccessful) {
                    return@withContext ResultResponse.Failed(Exception("No response"))
                }
                if (response.code() == 200) {
                    return@withContext ResultResponse.Success(Unit)
                }
                return@withContext ResultResponse.Failed(Exception("Failed with error code is ${response.code()}"))
            } catch (e: Exception) {
                Log.i(classTag(), "Failed with exception $e")
                return@withContext ResultResponse.Failed(Exception("Failed with exception $e"))
            }
        }
    }
}