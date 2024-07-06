package com.example.chattingApp.ui.screens.chatscreen

import com.example.chattingApp.domain.model.Message

sealed class ChatScreenEvent {
    data class SendMessage(val message: Message): ChatScreenEvent()
    data class ObserverMessages(val conversationId: String) : ChatScreenEvent()
}