package com.example.chattingApp.di

import com.example.chattingApp.data.repository.AuthRepositoryImpl
import com.example.chattingApp.data.repository.ChatRepositoryImpl
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModules {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl : ChatRepositoryImpl
    ): ChatRepository
}