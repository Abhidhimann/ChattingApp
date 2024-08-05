// data/NotificationHelper.kt
package com.example.app.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.chattingApp.R
import com.example.chattingApp.ui.MainActivity
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {

    private val channelId = "AbhiChattingApp"
    private val channelName = "Chatting App Channel"
    private val channelDescription = "Chatting App Channel for Notifications"

    private fun createNotificationChannelIfNotCreated() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = channelDescription
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun sendNotification(messageBody: String?) {
        createNotificationChannelIfNotCreated()

        // just opening app for now
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.google_icon)
            .setContentTitle("FCM Message Try")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}
