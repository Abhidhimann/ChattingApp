package com.example.chattingApp.data.remote.services.chatsocket

import android.util.Log
import com.example.chattingApp.data.remote.dto.MessageResponse
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

//import com.google.firebase.database.getValue

class ChatSocketServiceImp(private val db: FirebaseFirestore) : ChatSocketService {

    override suspend fun sendMessage(messageResponse: MessageResponse): ResultResponse<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = db.runTransaction { transaction ->
                    val docRef =
                        db.collection("singleChat").document(messageResponse.conversationId)
                            .collection("messages")
                            .document()
                    transaction.set(
                        docRef,
                        messageResponse
                    ) // Perform the write operation inside the transaction
                    transaction.update(
                        docRef,
                        "messageId",
                        docRef.id
                    )
                    docRef.id // Return the document ID from the transaction
                }.await()
                Log.i(classTag(), "message added")
                if (result == null) {
                    return@withContext ResultResponse.Failed(Exception("Result is null"))
                }
                return@withContext ResultResponse.Success(result)
            } catch (e: Exception) {
                Log.i(classTag(), "error in adding message $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun observeMessages(conversationId: String): Flow<MessageResponse> =
        callbackFlow {
            val messagesRef =
                db.collection("singleChat/$conversationId/messages")
                    .orderBy("timeStamp")

            val listenerRegistration = messagesRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(classTag(), "error in observing message -> $e")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    for (document in snapshots.documentChanges) {
                        val message = document.document.toObject(MessageResponse::class.java)
                        Log.i(tempTag(), "got message $message")
                        if (message != null) {
                            trySend(message)
                        }
                    }
                } else {
                    Log.d(classTag(), "No messages found")
                }
            }
            awaitClose { listenerRegistration.remove() }
        }
}