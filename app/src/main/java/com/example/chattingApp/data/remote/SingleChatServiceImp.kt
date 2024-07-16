package com.example.chattingApp.data.remote

import android.util.Log
import com.example.chattingApp.data.remote.dto.SingleChatDto
import com.example.chattingApp.data.remote.dto.UserSummaryDto
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.sin

class SingleChatServiceImp(private val db: FirebaseFirestore) : SingleChatService {
    override suspend fun createSingleChat(
        originator: UserSummaryDto,
        recipient: UserSummaryDto
    ): Int {
        return withContext(Dispatchers.IO) {
            val singleChatDto = SingleChatDto(
                originator = originator,
                recipient = recipient,
                participantIds = listOf(originator.userId, recipient.userId)
            )
            try {
                val docRef = db.collection("singleChat").add(singleChatDto).await()
                docRef.update("chatId", docRef.id)
                Log.i(classTag(), "chat created with id ${docRef.id}")
                return@withContext 1
            } catch (e: Exception) {
                Log.i(classTag(), "error in creating chat $e")
                return@withContext -1
            }
        }
    }

    override suspend fun updateUserSummaryInChat(chatId: String, userSummary: UserSummaryDto): Int {
        return withContext(Dispatchers.IO) {
            try {
                val singleChatDto = getSingleChat(chatId)
                if (singleChatDto?.originator == null || singleChatDto.recipient == null) {
                    Log.i(tempTag(), "error in updateUserSummary, singleChatDto is null")
                    return@withContext -1
                }
                if (singleChatDto.originator!!.userId == userSummary.userId) {
                    db.collection("singleChat").document(chatId).update("originator", userSummary)
                        .await()
                    return@withContext 1
                } else if (singleChatDto.recipient!!.userId == userSummary.userId) {
                    db.collection("singleChat").document(chatId).update("recipient", userSummary)
                        .await()
                    return@withContext 1
                }
                Log.i(tempTag(), "No user exits in chat with userId ${userSummary.userId}")
                return@withContext -1
            } catch (e: Exception) {
                Log.e(classTag(), "error in updating user summary", e)
                return@withContext -1
            }
        }
    }

    override suspend fun getSingleChat(chatId: String): SingleChatDto? {
        return withContext(Dispatchers.IO) {
            try {
                val singleChatSnapshot = db.collection("singleChat").document(chatId).get().await()
                return@withContext singleChatSnapshot.toObject(SingleChatDto::class.java)
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }

    override suspend fun observeSingleChats(userId: String): Flow<SingleChatDto> =
        callbackFlow {
            val querySnapshot =
                db.collection("singleChat").whereArrayContains("participantIds", userId)

            val listenerRegistration = querySnapshot.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(classTag(), "error in getting single chats -> $e")
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { documentChange ->
                    val singleChat = documentChange.document.toObject(SingleChatDto::class.java)
                    trySend(singleChat).isSuccess
                }
            }
            awaitClose { listenerRegistration.remove() }
        }


    override suspend fun acceptConnectRequestAndCreateChat(
        toUser: UserSummaryDto,
        fromUser: UserSummaryDto
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val toUserIncomingRequestRef = db.collection("users_details")
                    .document(toUser.userId)
                    .collection("incoming_requests")
                    .document(fromUser.userId)

                val fromUserOutgoingRequestRef = db.collection("users_details")
                    .document(fromUser.userId)
                    .collection("outgoing_requests")
                    .document(toUser.userId)

                val toUserFriendRef = db.collection("users_details")
                    .document(toUser.userId)
                    .collection("friends")
                    .document(fromUser.userId)

                val fromUserFriendRef = db.collection("users_details")
                    .document(fromUser.userId)
                    .collection("friends")
                    .document(toUser.userId)

                val singleChatRef = db.collection("singleChat").document()

                val result = db.runTransaction { transaction ->
                    transaction.delete(toUserIncomingRequestRef)
                    transaction.delete(fromUserOutgoingRequestRef)
                    transaction.set(toUserFriendRef, mapOf("userId" to fromUser.userId))
                    transaction.set(fromUserFriendRef, mapOf("userId" to toUser.userId))

                    val singleChatDto = SingleChatDto(
                        chatId = singleChatRef.id,
                        originator = fromUser,
                        recipient = toUser,
                        participantIds = listOf(toUser.userId, fromUser.userId)
                    )

                    transaction.set(singleChatRef, singleChatDto)
                    transaction.update(singleChatRef, "chatId", singleChatRef.id)

                    1
                }.await()

                return@withContext result
            } catch (e: Exception) {
                Log.e(classTag(), "Error in acceptConnectRequestAndCreateChat: $e")
                return@withContext -1
            }
        }
    }
}