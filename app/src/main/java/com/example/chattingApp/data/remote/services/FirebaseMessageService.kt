package com.example.chattingApp.data.remote.services

import android.content.SharedPreferences
import android.util.Log
import com.example.app.data.NotificationHelper
import com.example.chattingApp.data.remote.services.user.UserService
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


    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var userServiceRepository: UserServiceRepository

    private val myJob = Job()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(classTag(), "From: " + remoteMessage.from)
        remoteMessage.data.isNotEmpty().let {
            Log.d(classTag(), "Message data payload: " + remoteMessage.data)
        }
        remoteMessage.notification?.let {
            Log.d(classTag(), "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(classTag(), "Refresh token is $token")
        CoroutineScope(Dispatchers.IO + myJob).launch {
            userServiceRepository.saveUserTokenLocally(token)
            userServiceRepository.updateUserToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myJob.cancel()
    }
}