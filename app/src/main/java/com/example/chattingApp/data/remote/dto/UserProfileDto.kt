package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.UserProfile
import com.google.firebase.firestore.PropertyName

data class UserProfileDto(
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String,
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String,
    @get:PropertyName("password")
    @set:PropertyName("password")
    var password: String,
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String,
    @get:PropertyName("profile_img_url")
    @set:PropertyName("profile_img_url")
    var profileImageUrl: String,
    @get:PropertyName("gender")
    @set:PropertyName("gender")
    var gender: String,
    @get:PropertyName("about_me")
    @set:PropertyName("about_me")
    var aboutMe: String,
    @get:PropertyName("interests")
    @set:PropertyName("interests")
    var interests: String,
    @get:PropertyName("age")
    @set:PropertyName("age")
    var age: Int
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
            email = email,
            password = password,
            profileImageUrl = profileImageUrl,
            userId = userId,
            gender = gender,
            age = age,
            aboutMe = aboutMe,
            interests = interests
        )
    }
}