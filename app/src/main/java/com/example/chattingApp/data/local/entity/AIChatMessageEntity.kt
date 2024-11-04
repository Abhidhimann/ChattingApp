package com.example.chattingApp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.AIChatMessageType

@Entity(tableName = "ai_chat_messages")
data class AIChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String,
    val content: String,
    val type: Int,
    val timestamp: Long
) {
    fun toAIChatMessage(): AIChatMessage {
        val type = if (type == AIChatMessageType.INCOMING.value) {
            AIChatMessageType.INCOMING
        } else AIChatMessageType.OUTGOING
        return AIChatMessage(id = id, role = role, content = content, type = type, createdAt = timestamp)
    }
}