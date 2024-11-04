package com.example.chattingApp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chattingApp.data.local.dao.AIChatMessageDao
import com.example.chattingApp.data.local.entity.AIChatMessageEntity

@Database(entities = [AIChatMessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aiChatMessageDao(): AIChatMessageDao
}