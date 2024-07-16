package com.example.chattingApp.data.remote

import android.util.Log
import com.example.chattingApp.data.remote.dto.MessageDto
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

//import com.google.firebase.database.getValue

class ChatSocketServiceImp(private val db: FirebaseFirestore) : ChatSocketService {

    override suspend fun sendMessage(messageDto: MessageDto): Int {
        try {

            val result = db.runTransaction { transaction ->
                val docRef =
                    db.collection("singleChat").document(messageDto.conversationId)
                        .collection("messages")
                        .document()
                transaction.set(
                    docRef,
                    messageDto
                ) // Perform the write operation inside the transaction
                transaction.update(
                    docRef,
                    "messageId",
                    docRef.id
                ) // Update the document with the generated ID
                docRef.id // Return the document ID from the transaction
            }.await()
            Log.i(classTag(), "message added")
            return 1
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
            return -1
        }
    }

    override suspend fun observeMessages(conversationId: String): Flow<MessageDto> = callbackFlow {
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
                    val message = document.document.toObject(MessageDto::class.java)
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