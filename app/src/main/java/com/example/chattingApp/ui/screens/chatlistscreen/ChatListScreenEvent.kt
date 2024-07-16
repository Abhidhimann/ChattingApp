package com.example.chattingApp.ui.screens.chatlistscreen

sealed class ChatListScreenEvent {
    data object ObserveConversations: ChatListScreenEvent()
    data class OpenConversation(val conversationId: String): ChatListScreenEvent()
}