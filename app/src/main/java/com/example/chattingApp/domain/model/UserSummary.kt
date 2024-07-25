package com.example.chattingApp.domain.model

import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import java.time.Instant


// short version of user ( only on app side)
data class UserSummary(
    var name: String = "",
    var userId: String = "",
    var profileImageUrl: String = "",
    var createdAt: Instant? = null,
) {
    fun toUserSummaryDto(): UserSummaryResponse {
        return UserSummaryResponse(name = name, userId = userId, profileImageUrl = profileImageUrl)
    }
}


val tempUserSummary = UserSummary(
    name = "Abhishek dhiman",
    userId = "abc123",
    profileImageUrl = "",
)