package com.example.chattingApp.presentation.ui.screens.aichatbot

sealed class AIChatBotScreenEvent {
    data class SendQuery(val text: String) : AIChatBotScreenEvent()
    data object OnBackButtonPressed : AIChatBotScreenEvent()
}