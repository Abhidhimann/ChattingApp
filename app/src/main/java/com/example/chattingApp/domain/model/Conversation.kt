package com.example.chattingApp.domain.model

import java.time.Instant


data class Conversation(
    val conversationId: String,
    var updateAt: Instant? = null,
    val title: String,
    var iconUri: String = "",
    val unReadMessageCount: Int,
    val lastMessage: String,
    val participantsIds: List<String>,
    val currentUserId: String = "",
    var toUserId: String = ""
)

val tempConversations = listOf(
    Conversation(
        "",
        title = "Abhishek",
        iconUri = "",
        unReadMessageCount = 1,
        lastMessage = "how are you",
        participantsIds = emptyList()
    ),
    Conversation(
        "",
        title = "Abhishek",
        iconUri = "",
        unReadMessageCount = 1,
        lastMessage = "how are you",
        participantsIds = emptyList()
    ),
    Conversation(
        "",
        title = "Abhishek",
        iconUri = "",
        unReadMessageCount = 1,
        lastMessage = "how are you",
        participantsIds = emptyList()
    )
)