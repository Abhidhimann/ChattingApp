package com.example.chattingApp.presentation.ui.screens.aichatbot

import com.example.chattingApp.domain.model.AIChatMessage

data class AIChatBotScreenState (
    val aiChatMessages: List<AIChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSomeError: Boolean = false,
    val isLimitExceed: Boolean = false
)