package com.example.chattingApp.data.system

import android.util.Log
import com.example.chattingApp.domain.repository.PnsRepository
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.utils.classTag
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessageService @Inject constructor(
) : FirebaseMessagingService() {


    @Inject
    lateinit var pnsRepository: PnsRepository
    @Inject
    lateinit var userServiceRepository: UserServiceRepository

    private val myJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + myJob)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(classTag(), "From: " + remoteMessage.from)

        remoteMessage.data.let {
            Log.d(classTag(), "Message Notification Body: $it")
            coroutineScope.launch {
                pnsRepository.handleReceivedPns(it)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(classTag(), "Refresh token is $token")
        coroutineScope.launch {
            userServiceRepository.saveUserTokenLocally(token)
            userServiceRepository.updateUserToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myJob.cancel()
    }
}