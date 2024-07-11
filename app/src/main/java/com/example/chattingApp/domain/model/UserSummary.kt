package com.example.chattingApp.domain.model



// short version of user ( only on app side)
data class UserSummary(
    var name: String = "",
    var userId: String = "",
    var profileImageUrl: String = "",
)

val tempUserSummary = UserSummary(
    name = "Abhishek dhiman",
    userId = "abc123",
    profileImageUrl = "",
)