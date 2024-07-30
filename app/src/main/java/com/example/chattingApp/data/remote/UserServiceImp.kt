package com.example.chattingApp.data.remote

import android.util.Log
import com.example.chattingApp.data.remote.dto.MessageResponse
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserProfileResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserServiceImp(private val db: FirebaseFirestore) : UserService {

    // todo change user then it will be o(1) instead of o(n) in
    override suspend fun createUser(userDto: UserProfileResponse): ResultResponse<UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                db.collection("users_details").document(userDto.userId).set(userDto).await()
                return@withContext ResultResponse.Success(userDto)
            } catch (e: Exception) {
                Log.i(classTag(), "error in creating user $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun isUserExists(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext db.collection("users_details").document(userId).get().await()
                    .exists()
            } catch (e: Exception) {
                Log.i(classTag(), "error in fetching user $e")
                return@withContext false
            }
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfileResponse): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                db.collection("users_details").document(userProfile.userId).set(userProfile)
                    .await()
                return@withContext ResultResponse.Success(Unit)
            } catch (e: Exception) {
                Log.i(classTag(), "error in adding message $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun sendConnectionRequest(
        toUserSummary: UserSummaryResponse,
        fromUserSummary: UserSummaryResponse
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val toUserIncomingRequestRef =
                    db.collection("users_details").document(toUserSummary.userId)
                        .collection("incoming_requests")
                        .document(fromUserSummary.userId)
                val fromUserOutgoingRequestRef =
                    db.collection("users_details").document(fromUserSummary.userId)
                        .collection("outgoing_requests")
                        .document(toUserSummary.userId)
                // foreign key user_id in tables from user_details
                db.runTransaction { transaction ->
                    transaction.set(
                        toUserIncomingRequestRef,
                        fromUserSummary
                    )
                    transaction.set(
                        fromUserOutgoingRequestRef,
                        toUserSummary
                    )
                    Log.i(tempTag(), "Transaction successfully committed")
                }.await()
                return@withContext ResultResponse.Success(Unit)
            } catch (e: Exception) {
                Log.e(tempTag(), "send connection transaction failed", e)
                return@withContext ResultResponse.Failed(e)
            }
        }
    }


    override suspend fun removeConnectRequest(
        toUserId: String,
        fromUserId: String
    ): ResultResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val toUserIncomingRequestRef =
                    db.collection("users_details").document(toUserId)
                        .collection("incoming_requests")
                        .document(fromUserId)
                val fromUserOutgoingRequestRef =
                    db.collection("users_details").document(fromUserId)
                        .collection("outgoing_requests")
                        .document(toUserId)
                db.runTransaction { transaction ->
                    transaction.delete(toUserIncomingRequestRef)
                    transaction.delete(fromUserOutgoingRequestRef)
                    Log.i(tempTag(), "Transaction successfully committed")
                }.await()
                return@withContext ResultResponse.Success(Unit)
            } catch (e: Exception) {
                return@withContext ResultResponse.Failed(e)
            }
        }
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

    // todo see if need to listen this change or not as if user is some other user profile screen
    // then details will only change at opening use profile again.
    override suspend fun getUserProfileDetails(userId: String): ResultResponse<UserProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(tempTag(), "request userid is $userId")
                val userProfileResponse =
                    db.collection("users_details").document(userId).get().await()
                        .toObject(UserProfileResponse::class.java)
                if (userProfileResponse != null) {
                    return@withContext ResultResponse.Success(userProfileResponse)
                } else {
                    return@withContext ResultResponse.Failed(Exception("user profile $userId is null"))
                }
            } catch (e: Exception) {
                Log.i(classTag(), "error in getting userProfile $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun getUserFriendsDetails(userId: String): ResultResponse<List<UserSummaryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val friendSnapshot =
                    db.collection("users_details").document(userId).collection("friends").get()
                        .await()

                if (friendSnapshot != null && !friendSnapshot.isEmpty) {
                    return@withContext ResultResponse.Success(friendSnapshot.documents.mapNotNull {
                        it.toObject(
                            UserSummaryResponse::class.java
                        )!!
                    })
                } else {
                    return@withContext ResultResponse.Failed(Exception("get user friends snapshot $userId is empty"))
                }
            } catch (e: Exception) {
                Log.i(tempTag(), "error in fetching user friends -> $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    /*
     * can remove this "getUserProfileDetails(document.id)" by duplication
     * instead of storing user_id store user summary but then every time user changes
     * name or profile pic have to send push notification
     * i think duplication is much better so todo
     */
    override suspend fun getIncomingConnectRequestingUsers(userId: String): ResultResponse<List<UserSummaryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val incomingRequestSnapshot =
                    db.collection("users_details").document(userId).collection("incoming_requests")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .await()

                if (incomingRequestSnapshot != null && !incomingRequestSnapshot.isEmpty) {
                    return@withContext ResultResponse.Success(incomingRequestSnapshot.documents.map {
                        it.toObject(
                            UserSummaryResponse::class.java
                        )!!
                    })
                } else {
                    return@withContext ResultResponse.Failed(Exception("UserIncomingConnect request snapshot $userId is emptp"))
                }
            } catch (e: Exception) {
                Log.i(tempTag(), "error in fetching user friends -> $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    override suspend fun getOutgoingConnectRequestingUsers(userId: String): ResultResponse<List<UserSummaryResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val outgoingRequestSnapshot =
                    db.collection("users_details").document(userId).collection("outgoing_requests")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .await()

                if (outgoingRequestSnapshot != null && !outgoingRequestSnapshot.isEmpty) {
                    return@withContext ResultResponse.Success(outgoingRequestSnapshot.documents.map {
                        it.toObject(
                            UserSummaryResponse::class.java
                        )!!
                    })
                } else {
                    return@withContext ResultResponse.Failed(Exception("User outgoing Connect snapshot $userId is emptp"))
                }
            } catch (e: Exception) {
                Log.i(tempTag(), "error in fetching user friends -> $e")
                return@withContext ResultResponse.Failed(e)
            }
        }
    }

    // todo later replace it with PNS -> db -> observe
    override suspend fun observeConnectionRequests(userId: String): Flow<UserSummaryResponse?> =
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
                    for (document in snapshots.documentChanges) {
                        val userSummary =
                            document.document.toObject(UserSummaryResponse::class.java)
                        Log.i(tempTag(), "user $userSummary")
                        trySend(userSummary)
                    }
                } else {
                    Log.d(classTag(), "No users found ${snapshots?.isEmpty}")
                    trySend(null)
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
                    transaction.set(toUserFriendRef, fromUser)
                    transaction.set(fromUserFriendRef, toUser)

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