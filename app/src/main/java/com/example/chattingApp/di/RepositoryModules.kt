package com.example.chattingApp.di

import com.example.chattingApp.data.repository.AuthRepositoryImpl
import com.example.chattingApp.data.repository.ChatRepositoryImpl
import com.example.chattingApp.data.repository.ConversationRepositoryImpl
import com.example.chattingApp.data.repository.UserServiceRepositoryImpl
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.domain.repository.ChatRepository
import com.example.chattingApp.domain.repository.ConversationRepository
import com.example.chattingApp.domain.repository.UserServiceRepository
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
    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        conversationRepositoryImpl: ConversationRepositoryImpl
    ): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindUserServiceRepository(
        userServiceRepositoryImpl: UserServiceRepositoryImpl
    ): UserServiceRepository

}