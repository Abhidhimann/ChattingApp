package com.example.chattingApp.data.repository

import android.util.Log
import com.example.chattingApp.data.remote.dto.MessageNotification
import com.example.chattingApp.data.remote.services.pns.PnsService
import com.example.chattingApp.domain.repository.PnsRepository
import com.example.chattingApp.presentation.ui.util.NotificationHelper
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PnsRepositoryImpl @Inject constructor(
    private val pnsService: PnsService,
    private val notificationHelper: NotificationHelper
) : PnsRepository {

    private val gson = Gson()
    override suspend fun sendMessagePns(
        chatRoomId: String,
        chatRoomTitle: String,
        messageId: String,
        senderId: String,
        textContent: String
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    pnsService.sendMessagePns(
                        MessageNotification(
                            chatRoomId,
                            chatRoomTitle,
                            messageId,
                            senderId,
                            textContent
                        )
                    )
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

    override suspend fun handleReceivedPns(pns: Map<String, String>): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val pnsType = pns["pnsType"]
                val body = pns["body"]

                if (pnsType == null || body == null) {
                    return@withContext ResultResponse.Failed(Exception("Invalid PNS data"))
                }

                return@withContext when (pnsType) {
                    "message" -> {
                        val messageNotificationBody =
                            gson.fromJson(body, MessageNotification::class.java)
                                ?: return@withContext ResultResponse.Failed(Exception("Pns body is unknown"))
                        Log.i(classTag(), "message notification is $messageNotificationBody")
                        handleMessageNotification(messageNotificationBody)
                        ResultResponse.Success(Unit)
                    }

                    else -> {
                        ResultResponse.Failed(Exception("Unknown pns"))
                    }
                }
            } catch (e: Exception) {
                return@withContext ResultResponse.Failed(e)
            }
        }
    }


    private suspend fun handleMessageNotification(messageNotification: MessageNotification)  = withContext(Dispatchers.IO){
        // todo get message and then save in local db after that send notification
        notificationHelper.sendNotification(messageNotification)
    }
}