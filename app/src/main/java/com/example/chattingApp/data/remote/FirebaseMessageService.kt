package com.example.chattingApp.data.remote
import android.util.Log
import com.example.chattingApp.utils.classTag
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(classTag(),  "From: " + message.from)
        if (message.getNotification() != null) {
            Log.d(classTag(), "Notification Message Body: " + message.getNotification()!!.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(classTag(),  "Refresh token $token")
    }
}