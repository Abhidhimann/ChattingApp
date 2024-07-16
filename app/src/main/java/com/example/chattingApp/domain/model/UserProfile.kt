package com.example.chattingApp.domain.model

import com.example.chattingApp.data.remote.dto.UserProfileResponse


data class UserProfile(
    var name: String,
    var userId: String,
    var profileImageUrl: String,
    var gender: UserGender,
    var aboutMe: String,
    var interests: String,
    var online: Boolean,
    var incomingRequests: List<UserSummary> = emptyList(),
    var outgoingRequests: List<UserSummary> = emptyList(),
    var friends: List<UserSummary> = emptyList(),
    var relation: UserRelation = UserRelation.NON_FRIEND
) {
    fun toUserProfileDto(): UserProfileResponse {
        return UserProfileResponse(
            name = name,
            userId = userId,
            profileImageUrl = profileImageUrl,
            gender = gender.value,
            aboutMe = aboutMe,
            interests = interests,
            online = online
        )
    }

    fun toUserSummary(): UserSummary {
        return UserSummary(name = name, userId = userId, profileImageUrl = profileImageUrl)
    }
}

enum class UserGender(val value: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others"),
}

enum class UserRelation {
    FRIEND,
    NON_FRIEND,
    ALREADY_REQUESTED
}

val tempUserProfile = UserProfile(
    name = "Abhishek dhiman",
    userId = "abc123",
    profileImageUrl = "",
    gender = UserGender.MALE,
    aboutMe = "This is nothing about me, This is nothing about me, This is nothing about me, This is nothing about me",
    interests = "No interests",
    online = false
)