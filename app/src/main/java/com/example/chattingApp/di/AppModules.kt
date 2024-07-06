package com.example.chattingApp.di

import com.example.chattingApp.data.remote.ChatSocketService
import com.example.chattingApp.data.remote.ChatSocketServiceImp
import com.example.chattingApp.data.remote.FirebaseMessageService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Singleton
    fun providesFirebaseDatabase(): FirebaseFirestore {
        return Firebase.firestore
    }
    @Provides
    @Singleton
    fun providesChatSocketService(firebaseDatabase: FirebaseFirestore): ChatSocketService {
        return ChatSocketServiceImp(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun providesFirebaseMessageService(): FirebaseMessageService {
        return FirebaseMessageService()
    }

}