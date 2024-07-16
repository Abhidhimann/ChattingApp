package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.UserProfile
import com.google.firebase.firestore.PropertyName

data class UserProfileResponse(
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    @get:PropertyName("profile_img_url")
    @set:PropertyName("profile_img_url")
    var profileImageUrl: String = "",
    @get:PropertyName("gender")
    @set:PropertyName("gender")
    var gender: String = "",
    @get:PropertyName("about_me")
    @set:PropertyName("about_me")
    var aboutMe: String = "",
    @get:PropertyName("interests")
    @set:PropertyName("interests")
    var interests: String = "",
    @get:PropertyName("online")
    @set:PropertyName("online")
    var online: Boolean = false,
) {
    fun toUserProfile(): UserProfile {
        val gender = when (gender) {
            UserGender.MALE.value -> UserGender.MALE
            UserGender.FEMALE.value -> UserGender.FEMALE
            UserGender.OTHERS.value -> UserGender.OTHERS
            else -> {
                UserGender.OTHERS
            }
        }
        return UserProfile(
            name = name,
            profileImageUrl = profileImageUrl,
            userId = userId,
            gender = gender,
            aboutMe = aboutMe,
            interests = interests,
            online = online,
        )
    }
}