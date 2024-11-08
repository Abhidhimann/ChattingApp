package com.example.chattingApp

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.utils.classTag
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ChatApp : Application() {

    @Inject
    lateinit var appPref: SharedPreferences
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        saveFCMTokenIfNew()
        fetchRemoteConfig()
    }


    private fun saveFCMTokenIfNew() {
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

    private fun fetchRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        FirebaseRemoteConfig.getInstance().fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newAiModel: String = mFirebaseRemoteConfig.getString("AI_MODEL")
                    Log.d(classTag(), "Fetched AI MODEL : $newAiModel")
                    val preAIModel = appPref.getString("ai_model", null)
                    if (preAIModel != null && preAIModel == newAiModel) {
                        return@addOnCompleteListener
                    }
                    appPref.edit().putString("ai_model", newAiModel).apply()
                } else {
                    Log.e(classTag(), "Remote config fetch failed")
                }
            }
    }
}