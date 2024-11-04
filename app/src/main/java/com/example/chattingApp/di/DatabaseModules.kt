package com.example.chattingApp.di

import android.content.Context
import androidx.room.Room
import com.example.chattingApp.data.local.dao.AIChatMessageDao
import com.example.chattingApp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModules {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAIChatMessageDao(database: AppDatabase): AIChatMessageDao {
        return database.aiChatMessageDao()
    }
}