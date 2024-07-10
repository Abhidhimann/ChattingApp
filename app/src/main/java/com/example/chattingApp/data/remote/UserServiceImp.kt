package com.example.chattingApp.data.remote

import android.util.Log
import com.example.chattingApp.data.remote.dto.UserProfileDto
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserServiceImp(private val db: FirebaseFirestore) : UserService {
    override suspend fun createUser(): Result<Any> {
        val userDto = UserProfileDto()
        val userId = UUID.randomUUID().toString()
        userDto.userId = userId
        try {
            val docRef = db.collection("users_details").add(userDto).await()
            Log.i(classTag(), "user created with id ${docRef.id}")
            return Result.success(userId)
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
            return Result.failure(exception = e)
        }
    }

    override suspend fun updateUserProfile(userId: String, userProfile: UserProfileDto): Int {
        try {
            db.collection("users_details").document(userId).set(userProfile)
                .await()
            return 1
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
        }
        return 0
    }

    override suspend fun findRandomUser(): String {
        return ""
    }

    override suspend fun updateUserOnlineStatus(userId: String, value: Boolean): Int {
        try {
            db.collection("users_details").document(userId).update("ready_to_chat", value)
                .await()
            return 1
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
            return -1
        }
    }

    override suspend fun sendConnectionRequest(toUserId: String, fromUserId: String): Int {
        Log.i(tempTag(), "getting here with $toUserId and $fromUserId")
        try {
            val querySnapshot = db.collection("users_details")
                .whereEqualTo("user_id", toUserId)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                val userDocument = querySnapshot.documents[0].reference
                userDocument.update("requests", FieldValue.arrayUnion(fromUserId)).await()
                Log.i(tempTag(), "connection request send successfully")
                return 1
            } else {
                Log.i(tempTag(), "No user found with userId: $fromUserId")
                return -1
            }
        } catch (e: Exception) {
            Log.i(classTag(), "error in sending connect request $e")
            return -1
        }
    }

    override suspend fun removeConnectRequest(toUserId: String, fromUserId: String): Int {
        try {
            db.collection("users_details").document(fromUserId)
                .update("friends", FieldValue.arrayRemove(toUserId))
                .await()
            return 1
        } catch (e: Exception) {
            Log.i(classTag(), "error in sending connect request $e")
            return -1
        }
    }

    override suspend fun observeNonConnectedUsers(
        fromUserId: String,
        friendsIdList: List<String>
    ): Flow<UserProfileDto> =
        callbackFlow {
            val querySnapshot =
                db.collection("users_details")
                    .whereNotIn("user_id", friendsIdList + listOf(fromUserId))

            val listenerRegistration = querySnapshot.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(classTag(), "error in observing message -> $e")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    for (document in snapshots.documentChanges) {
                        val user = document.document.toObject(UserProfileDto::class.java)
                        Log.i(tempTag(), "got user $user")
                        if (user != null) {
                            trySend(user)
                        }
                    }
                } else {
                    Log.d(classTag(), "No messages found")
                }
            }
            awaitClose { listenerRegistration.remove() }
        }

    // todo see if need to listen this change or not
    override suspend fun getUserProfileDetails(fromUserId: String): UserProfileDto? {
        return try {
            val friendsSnapshot =
                db.collection("users_details").whereEqualTo("user_id", fromUserId).get().await()

            if (friendsSnapshot.documents.isNotEmpty()) {
                val userDocument = friendsSnapshot.documents[0]
                userDocument.toObject(UserProfileDto::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.i(classTag(), "error in getting userProfile $e")
            null
        }
    }
}