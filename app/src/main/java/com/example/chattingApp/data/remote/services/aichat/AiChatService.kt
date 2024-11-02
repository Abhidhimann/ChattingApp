package com.example.chattingApp.data.remote.services.aichat

import com.example.chattingApp.data.remote.dto.AIChatRequestBody
import com.example.chattingApp.data.remote.dto.AIChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AiChatService {

    @Headers("Content-Type: application/json")
    @POST("/v1/chat/completions")
    suspend fun getChatBotResponse(
        @Body requestBody: AIChatRequestBody,
    ): AIChatResponse
}

