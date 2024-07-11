package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.UserSummary
import com.google.firebase.firestore.PropertyName

// delete this class
data class UserSummaryDto (
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    @get:PropertyName("profile_img_url")
    @set:PropertyName("profile_img_url")
    var profileImageUrl: String = "",
) {
    fun toUserSummary(): UserSummary {
        return UserSummary(name = name, userId = userId, profileImageUrl = profileImageUrl)
    }
}