package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.data.local.entity.AIChatMessageEntity
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

    fun toAIChatMessageEntity(createdAt: Long, type: Int): AIChatMessageEntity {
        return AIChatMessageEntity(
            role = role,
            content = content,
            timestamp = createdAt,
            type = type
        )
    }
}
