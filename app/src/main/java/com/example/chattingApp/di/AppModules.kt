package com.example.chattingApp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.chattingApp.data.remote.ChatSocketService
import com.example.chattingApp.data.remote.ChatSocketServiceImp
import com.example.chattingApp.data.remote.ImageService
import com.example.chattingApp.data.remote.ImageServiceImpl
import com.example.chattingApp.data.remote.SingleChatService
import com.example.chattingApp.data.remote.SingleChatServiceImp
import com.example.chattingApp.data.remote.UserService
import com.example.chattingApp.data.remote.UserServiceImp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    fun providesFirebaseStorage(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    @Provides
    @Singleton
    fun providesChatSocketService(firebaseDatabase: FirebaseFirestore): ChatSocketService {
        return ChatSocketServiceImp(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun providesUserService(firebaseDatabase: FirebaseFirestore): UserService {
        return UserServiceImp(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun providesImageService(storageRef: StorageReference): ImageService {
        return ImageServiceImpl(storageRef)
    }

    @Provides
    @Singleton
    fun providesAppSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesSingleChatService(firebaseDatabase: FirebaseFirestore): SingleChatService {
        return SingleChatServiceImp(firebaseDatabase)
    }

//    @Provides
//    @Singleton
//    fun providesFirebaseMessageService(): FirebaseMessageService {
//        return FirebaseMessageService()
//    }

}