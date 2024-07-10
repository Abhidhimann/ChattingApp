package com.example.chattingApp.domain.model

import com.example.chattingApp.data.remote.dto.UserProfileDto


data class UserProfile(
    var name: String,
    var userId: String,
    var profileImageUrl: String,
    var gender: UserGender,
    var aboutMe: String,
    var interests: String,
    var online: Boolean,
    var requests: List<String> = emptyList(),
    var friends: List<String> = emptyList(),
    var relation: UserRelation = UserRelation.NON_FRIEND
) {
    fun toUserProfileDto(): UserProfileDto {
        return UserProfileDto(
            name = name,
            userId = userId,
            profileImageUrl = profileImageUrl,
            gender = gender.value,
            aboutMe = aboutMe,
            interests = interests,
            online = online,
            requests = requests,
            friends = friends
        )
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
    name = "Abhishek Dhiman",
    userId = "abc123",
    profileImageUrl = "",
    gender = UserGender.MALE,
    aboutMe = "This is nothing about me, This is nothing about me, This is nothing about me, This is nothing about me",
    interests = "No interests",
    online = false
)