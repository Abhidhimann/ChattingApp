package com.example.chattingApp.ui.screens.editprofilescreen

import com.example.chattingApp.domain.model.UserProfile

data class EditProfileScreenState(
    var profile: UserProfile? = null,
    val isLoading: Boolean = false
)