package com.example.chattingApp.presentation.ui.screens.aichatbot

sealed class AIChatBotScreenEvent {
    data object ObserverAIChatMessages: AIChatBotScreenEvent()
    data object DeleteAIChatConversation: AIChatBotScreenEvent()
    data class SendQuery(val text: String) : AIChatBotScreenEvent()
    data object OnBackButtonPressed : AIChatBotScreenEvent()
}