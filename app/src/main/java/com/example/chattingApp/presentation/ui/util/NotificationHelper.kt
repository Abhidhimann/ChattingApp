package com.example.chattingApp.presentation.ui.util

import android.app.Notification
import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.example.chattingApp.R
import com.example.chattingApp.data.remote.dto.MessageNotification
import com.example.chattingApp.presentation.ui.MainActivity
import com.example.chattingApp.presentation.ui.screens.Screen
import com.example.chattingApp.utils.START_DESTINATION
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {

    private val notificationMap = mutableMapOf<String, MutableList<String>>()
    private val chatRoomIdMap = mutableMapOf<String, Int>()
    private var currentId = 1

    private fun createNotificationChannelIfNotCreated() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DES
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun sendNotification(messageNotification: MessageNotification) {
        Log.i(classTag(), "notify start for $messageNotification")
        createNotificationChannelIfNotCreated()
        val updatedNotification =
            notificationMap[messageNotification.chatRoomId] ?: mutableListOf<String>()
        updatedNotification.add(messageNotification.textContent)
        notificationMap[messageNotification.chatRoomId] = updatedNotification

        // just opening app for now
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(START_DESTINATION, Screen.Chat.createRoute(messageNotification.chatRoomId))
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val person = Person.Builder()
            .setName(messageNotification.chatRoomTitle)
            .setKey(messageNotification.chatRoomId)
//            .setIcon() later with db todo
            .setImportant(true).build()

        val messageStyle = notificationMap[messageNotification.chatRoomId]?.let {
            getNotificationMessageStyle(
                person,
                it
            )
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.app_icon) // todo will change this to my app icon
            .setContentTitle(messageNotification.chatRoomTitle)
            .setStyle(messageStyle)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setContentText(messageNotification.textContent)
            .setDefaults(DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = if (chatRoomIdMap[messageNotification.chatRoomId] != null) {
            chatRoomIdMap[messageNotification.chatRoomId]!!
        } else {
            chatRoomIdMap[messageNotification.chatRoomId] = currentId
            currentId++
        }

        try {
            notificationManager.notify(
                notificationId,
                notificationBuilder.build()
            )
        } catch (e: Exception) {
            Log.i(classTag(), "Error in notify $e")
        }
    }

    private fun getNotificationMessageStyle(
        person: Person,
        messages: List<String>
    ): NotificationCompat.MessagingStyle {
        val messageStyle = NotificationCompat.MessagingStyle(person)

        Log.i(tempTag(), "mesasge are $messages")

        for (message in messages) {
            messageStyle.addMessage(
                NotificationCompat.MessagingStyle.Message(
                    message,
                    System.currentTimeMillis(),
                    person
                )
            )
        }
        return messageStyle
    }

    companion object {
        const val CHANNEL_ID = "ChattingAppChannel"
        const val CHANNEL_NAME = "Chatting App message channel"
        const val CHANNEL_DES = "Chatting App message channel description"
    }
}
