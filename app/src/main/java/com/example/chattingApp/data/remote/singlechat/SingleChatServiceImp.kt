package com.example.chattingApp.data.remote.singlechat

import android.util.Log
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SingleChatServiceImp(private val db: FirebaseFirestore) : SingleChatService {
    override suspend fun createSingleChat(
        originator: UserSummaryResponse,
        recipient: UserSummaryResponse
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val singleChatResponse = SingleChatResponse(
                    originator = originator,
                    recipient = recipient,
                    participantIds = listOf(originator.userId, recipient.userId)
                )
                val docRef = db.collection("singleChat").add(singleChatResponse).await()
                docRef.update("chatId", docRef.id)
                Log.i(classTag(), "chat created with id ${docRef.id}")
                return@withContext ResultResponse.Success(Unit)
            } catch (e: Exception) {
                Log.i(classTag(), "error in creating chat $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun updateUserSummaryInChat(
        singleChatDto: SingleChatResponse,
        userSummary: UserSummaryResponse
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (singleChatDto.originator == null || singleChatDto.recipient == null) {
                    Log.i(tempTag(), "error in updateUserSummary, singleChatDto is null")
                    return@withContext ResultResponse.Failed(Exception("error in updateUserSummary, singleChatDto is null"))
                }
                if (singleChatDto.originator!!.userId == userSummary.userId) {
                    db.collection("singleChat").document(singleChatDto.chatId)
                        .update("originator", userSummary)
                        .await()
                    return@withContext ResultResponse.Success(Unit)
                } else if (singleChatDto.recipient!!.userId == userSummary.userId) {
                    db.collection("singleChat").document(singleChatDto.chatId)
                        .update("recipient", userSummary)
                        .await()
                    return@withContext ResultResponse.Success(Unit)
                }
                Log.i(tempTag(), "No user exits in chat with userId ${userSummary.userId}")
                return@withContext ResultResponse.Failed(Exception("No user exits in chat with userId ${userSummary.userId}"))
            } catch (e: Exception) {
                Log.e(classTag(), "error in updating user summary", e)
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun getSingleChat(chatId: String): ResultResponse<SingleChatResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val singleChatSnapshot = db.collection("singleChat").document(chatId).get().await()
                val singleChatResponse = singleChatSnapshot.toObject(SingleChatResponse::class.java)
                if (singleChatSnapshot == null) {
                    return@withContext ResultResponse.Failed(Exception("singleChatSnapshot with chatId: $chatId is null"))
                }
                return@withContext ResultResponse.Success(singleChatResponse!!)
            } catch (e: Exception) {
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun observeSingleChats(userId: String): Flow<SingleChatResponse> =
        callbackFlow {
            val querySnapshot =
                db.collection("singleChat").whereArrayContains("participantIds", userId)

            val listenerRegistration = querySnapshot.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(classTag(), "error in getting single chats -> $e")
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { documentChange ->
                    val singleChat =
                        documentChange.document.toObject(SingleChatResponse::class.java)
                    trySend(singleChat).isSuccess
                }
            }
            awaitClose { listenerRegistration.remove() }
        }


    override suspend fun acceptConnectRequestAndCreateChat(
        toUser: UserSummaryResponse,
        fromUser: UserSummaryResponse
    ): ResultResponse<Unit> {
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

                db.runTransaction { transaction ->
                    transaction.delete(toUserIncomingRequestRef)
                    transaction.delete(fromUserOutgoingRequestRef)
                    transaction.set(toUserFriendRef, mapOf("userId" to fromUser.userId))
                    transaction.set(fromUserFriendRef, mapOf("userId" to toUser.userId))

                    val singleChatResponse = SingleChatResponse(
                        chatId = singleChatRef.id,
                        originator = fromUser,
                        recipient = toUser,
                        participantIds = listOf(toUser.userId, fromUser.userId)
                    )

                    transaction.set(singleChatRef, singleChatResponse)
                    transaction.update(singleChatRef, "chatId", singleChatRef.id)
                }.await()

                return@withContext ResultResponse.Success(Unit)
            } catch (e: Exception) {
                Log.e(classTag(), "Error in acceptConnectRequestAndCreateChat: $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }
}