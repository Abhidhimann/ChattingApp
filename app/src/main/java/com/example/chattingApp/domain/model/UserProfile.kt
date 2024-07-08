package com.example.chattingApp.domain.model


data class UserProfile(
    var name: String,
    var email: String,
    var password: String,
    var userId: String,
    var profileImageUrl: String,
    var gender: UserGender,
    var aboutMe: String,
    var interests: String,
    var age: Int
)

enum class UserGender(val value: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others"),
}

val tempUserProfile = UserProfile(
    name = "Abhishek Dhiman",
    email = "abc@gmail.com",
    password = "",
    userId = "abc123",
    profileImageUrl = "",
    gender = UserGender.MALE,
    aboutMe = "This is nothing about me, This is nothing about me, This is nothing about me, This is nothing about me",
    age = 21,
    interests = "No interests"
)