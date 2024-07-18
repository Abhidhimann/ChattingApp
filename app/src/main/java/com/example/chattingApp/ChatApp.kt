package com.example.chattingApp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
    }
}