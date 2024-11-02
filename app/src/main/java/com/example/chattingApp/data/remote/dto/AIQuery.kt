package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.AIChatMessageType
import com.google.gson.annotations.SerializedName

data class AIQuery(
    val role: String,
    val content: String,
    val max_tokens: Int = 20 // keeping it less so less token will get used.
)

data class AIChatRequestBody(
    val model: String,
    @SerializedName("messages")
    val messages: List<AIQuery>,
)

data class AIChatResponse(
    val created: Long,
    val choices: List<Choice>
)

data class Choice(
    val message: AIChatAnswer
)

data class AIChatAnswer(
    val role: String,
    val content: String,
) {
    fun toAIChatMessage(createdAt: Long, type: AIChatMessageType): AIChatMessage {
        return AIChatMessage(
            role = role,
            content = content,
            createdAt = createdAt,
            type = type
        )
    }
}
