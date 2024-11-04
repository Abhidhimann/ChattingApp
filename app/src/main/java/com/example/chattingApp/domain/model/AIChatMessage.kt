package com.example.chattingApp.domain.model

import com.example.chattingApp.data.local.entity.AIChatMessageEntity
import com.example.chattingApp.data.remote.dto.AIQuery

data class AIChatMessage(
    val id: Int,
    val role: String,
    val content: String,
    val createdAt: Long = 0L,
    val type: AIChatMessageType? = null
) {
    fun toAiQuery(): AIQuery {
        return AIQuery(role = role, content = content)
    }

    fun toAiChatMessageEntity(): AIChatMessageEntity {
        return AIChatMessageEntity(
            id = id,
            role = role,
            content = content,
            timestamp = createdAt,
            type = type?.value ?: 0
        )
    }
}


enum class AIChatMessageType(val value: Int) {
    INCOMING(0),
    OUTGOING(1)
}

val tempAIChatMessageList =
    listOf(AIChatMessage(1, "", "how are you"), AIChatMessage(1, "", "I'm good"))