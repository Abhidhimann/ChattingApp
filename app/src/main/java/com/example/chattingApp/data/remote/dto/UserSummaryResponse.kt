package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.UserSummary
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// delete this class
data class UserSummaryResponse (
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    @get:PropertyName("profile_img_url")
    @set:PropertyName("profile_img_url")
    var profileImageUrl: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,
) {
    fun toUserSummary(): UserSummary {
        return UserSummary(name = name, userId = userId, profileImageUrl = profileImageUrl, createdAt = createdAt?.toInstant())
    }
}