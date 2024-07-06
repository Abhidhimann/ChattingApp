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

    override suspend fun sendMessage(messageDto: MessageDto) {
//        val conversationId = UUID.randomUUID().toString()
//        messageDto.conversationId = conversationId
        try {
            val docRef = db.collection("messages").add(messageDto).await()
            Log.i(classTag(), "message added with id ${docRef.id}")
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
        }
    }

    override suspend fun observeMessages(conversationId: String): Flow<MessageDto> = callbackFlow {
        val messagesRef =
            db.collection("messages")
                .whereEqualTo("conversation_id", conversationId)
                .orderBy("time_stamp")

        val listenerRegistration = messagesRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.d(classTag(), "error in observing message -> $e")
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                for (document in snapshots.documentChanges) {
                    val message = document.document.toObject(MessageDto::class.java)
                    Log.i(tempTag(),"got message $message")
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