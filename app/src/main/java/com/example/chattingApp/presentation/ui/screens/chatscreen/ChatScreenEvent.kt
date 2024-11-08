package com.example.chattingApp.presentation.ui.screens.chatscreen

sealed class ChatScreenEvent {
    data class SendMessage(val textContent: String): ChatScreenEvent()
    data class ObserverMessages(val conversationId: String) : ChatScreenEvent()
    data class GetConversationDetails(val conversationId: String): ChatScreenEvent()
    data object OnBackButtonPressed: ChatScreenEvent()
    data object SummarizeConversation: ChatScreenEvent()
    data class UpdateCurrentChatRoom(val chatId: String?): ChatScreenEvent()
    data object SetDefaultValueForAISummary: ChatScreenEvent()
}