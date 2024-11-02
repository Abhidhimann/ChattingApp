package com.example.chattingApp.domain.model

import com.example.chattingApp.data.remote.dto.AIQuery

data class AIChatMessage(
    val role: String,
    val content: String,
    val createdAt: Long = 0L,
    val type: AIChatMessageType? = null
) {
    fun toAiQuery(): AIQuery {
        return AIQuery(role = role, content = content)
    }
}

enum class AIChatMessageType {
    INCOMING,
    OUTGOING
}

val tempAIChatMessageList = listOf(AIChatMessage("", "how are you"), AIChatMessage("", "I'm good"))