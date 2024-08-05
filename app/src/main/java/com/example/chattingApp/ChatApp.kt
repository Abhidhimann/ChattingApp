package com.example.chattingApp

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.utils.classTag
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ChatApp : Application() {

    @Inject
    lateinit var appPref: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        saveTokenIfNew()
    }

    private fun saveTokenIfNew() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(classTag(), "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val newToken = task.result
            Log.d(classTag(), "FCM registration token: $newToken")
            val previousToken = appPref.getString("token", null)
            if (previousToken != null && previousToken == newToken) {
                return@addOnCompleteListener
            }
            appPref.edit().putString("token", newToken).apply()
        }
    }
}