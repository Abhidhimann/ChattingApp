package com.example.chattingApp.data.remote

import android.util.Log
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserProfileResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserServiceImp(private val db: FirebaseFirestore) : UserService {

    // todo change user then it will be o(1) instead of o(n) in
    override suspend fun createUser(userDto: UserProfileResponse): ResultResponse<UserProfileResponse> {
        try {
            db.collection("users_details").document(userDto.userId).set(userDto).await()
            return ResultResponse.Success(userDto)
        } catch (e: Exception) {
            Log.i(classTag(), "error in creating user $e")
            return ResultResponse.Failed(e)
        }
    }

    override suspend fun getUserProfileDocumentReference(userId: String): DocumentReference? {
        try {
            return db.collection("users_details").document(userId)
        } catch (e: Exception) {
            Log.i(tempTag(), "Fetching user profile reference failed with $e")
            return null
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfileResponse): Int {
        try {
            db.collection("users_details").document(userProfile.userId).set(userProfile)
                .await()
            return 1
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
        }
        return 0
    }

    override suspend fun updateUserOnlineStatus(userId: String, value: Boolean): Int {
        try {
            db.collection("users_details").document(userId).update("ready_to_chat", value).await()
            return 1
        } catch (e: Exception) {
            Log.i(classTag(), "error in adding message $e")
            return -1
        }
    }

    override suspend fun sendConnectionRequest(
        toUserId: String,
        fromUserId: String
    ): Int {
        try {
            val toUserIncomingRequestRef =
                db.collection("users_details").document(toUserId).collection("incoming_requests")
                    .document(fromUserId)
            val fromUserOutgoingRequestRef =
                db.collection("users_details").document(fromUserId).collection("outgoing_requests")
                    .document(toUserId)
            // foreign key user_id in tables from user_details
            db.runTransaction { transaction ->
                transaction.set(
                    toUserIncomingRequestRef,
                    mapOf("user_id" to fromUserId, "created_at" to FieldValue.serverTimestamp())
                )
                transaction.set(
                    fromUserOutgoingRequestRef,
                    mapOf("user_id" to toUserId, "created_at" to FieldValue.serverTimestamp())
                )
                Log.i(tempTag(), "Transaction successfully committed")
            }.await()
            return 1
        } catch (e: Exception) {
            Log.e(tempTag(), "send connection transaction failed", e)
            return -1
        }
    }


    override suspend fun removeConnectRequest(
        toUserId: String,
        fromUserId: String
    ): Int {
        val toUserIncomingRequestRef =
            db.collection("users_details").document(toUserId).collection("incoming_requests")
                .document(fromUserId)
        val fromUserOutgoingRequestRef =
            db.collection("users_details").document(fromUserId).collection("outgoing_requests")
                .document(toUserId)
        val result = db.runTransaction { transaction ->
            transaction.delete(toUserIncomingRequestRef)
            transaction.delete(fromUserOutgoingRequestRef)
            Log.i(tempTag(), "Transaction successfully committed")
        }.await()
        return result
    }

    override suspend fun observeNonConnectedUsers(
        fromUserId: String,
        friendsIdList: List<String>
    ): Flow<UserProfileResponse> =
        callbackFlow {
            val querySnapshot =
                db.collection("users_details")
                    .whereNotIn("user_id", friendsIdList + listOf(fromUserId))

            val listenerRegistration = querySnapshot.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(classTag(), "error in observing users -> $e")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    for (document in snapshots.documentChanges) {
                        val user = document.document.toObject(UserProfileResponse::class.java)
                        Log.i(tempTag(), "got user $user")
                        if (user != null) {
                            trySend(user)
                        }
                    }
                } else {
                    Log.d(classTag(), "No users found")
                }
            }
            awaitClose { listenerRegistration.remove() }
        }

    // todo see if need to listen this change or not
    override suspend fun getUserProfileDetails(userId: String): UserProfileResponse? {
        return try {
            Log.i(tempTag(), "request userid is $userId")
            return db.collection("users_details").document(userId).get().await()
                .toObject(UserProfileResponse::class.java)
        } catch (e: Exception) {
            Log.i(classTag(), "error in getting userProfile $e")
            null
        }
    }

    override suspend fun getUserFriends(userId: String): List<UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val friendSnapshot =
                    db.collection("users_details").document(userId).collection("friends").get()
                        .await()

                if (friendSnapshot != null && !friendSnapshot.isEmpty) {
                    friendSnapshot.documents.map { document ->
                        async {
                            val friendUserId = document.id
                            getUserProfileDetails(friendUserId)
                        }
                    }.awaitAll().filterNotNull()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.i(tempTag(), "error in fetching user friends -> $e")
                emptyList()
            }
        }
    }

    /*
     * can remove this "getUserProfileDetails(document.id)" by duplication
     * instead of storing user_id store user summary but then every time user changes
     * name or profile pic have to send push notification
     * i think duplication is much better so todo
     */
    override suspend fun getUserIncomingConnectRequests(userId: String): List<UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val incomingRequestSnapshot =
                    db.collection("users_details").document(userId).collection("incoming_requests")
                        .orderBy("created_at", Query.Direction.DESCENDING)
                        .get()
                        .await()

                if (incomingRequestSnapshot != null && !incomingRequestSnapshot.isEmpty) {
                    incomingRequestSnapshot.documents.map { document ->
                        async {
                            getUserProfileDetails(document.id)
                        }
                    }.awaitAll().filterNotNull()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.i(tempTag(), "error in fetching user friends -> $e")
                emptyList()
            }
        }
    }

    override suspend fun getUserOutgoingConnectRequests(userId: String): List<UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val outgoingRequestSnapshot =
                    db.collection("users_details").document(userId).collection("outgoing_requests")
                        .get()
                        .await()

                if (outgoingRequestSnapshot != null && !outgoingRequestSnapshot.isEmpty) {
                    outgoingRequestSnapshot.documents.map { document ->
                        async {
                            getUserProfileDetails(document.id)
                        }
                    }.awaitAll().filterNotNull()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.i(tempTag(), "error in fetching user friends -> $e")
                emptyList()
            }
        }
    }

    override suspend fun observeConnectionRequests(userId: String): Flow<UserProfileResponse> =
        callbackFlow {
            val querySnapshot =
                db.collection("/users_details/$userId/incoming_requests")
//            db.collection("users_details").document(userId).collection("incoming_requests")
//             this is not working interesting

            val listenerRegistration = querySnapshot.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(classTag(), "error in incoming_requests -> $e")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    launch(Dispatchers.IO) {
                        val jobs = snapshots.documentChanges.map { documentChange ->
                            async {
                                val requestUserId = documentChange.document.id
                                val userProfile = getUserProfileDetails(requestUserId)
                                userProfile?.let { trySend(it).isSuccess }
                            }
                        }
                        jobs.awaitAll()
                    }
                } else {
                    Log.d(classTag(), "No users found ${snapshots?.isEmpty}")
                }
            }
            awaitClose { listenerRegistration.remove() }
        }

    override suspend fun acceptConnectRequestAndCreateChat(
        toUser: UserSummaryResponse,
        fromUser: UserSummaryResponse
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

                    val singleChatResponse = SingleChatResponse(
                        chatId = singleChatRef.id,
                        originator = fromUser,
                        recipient = toUser,
                        participantIds = listOf(toUser.userId, fromUser.userId)
                    )

                    transaction.set(singleChatRef, singleChatResponse)
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