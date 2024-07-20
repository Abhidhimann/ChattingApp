package com.example.chattingApp.ui.screens.editprofilescreen

import com.example.chattingApp.domain.model.UserProfile

data class EditProfileScreenState(
    var userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    var updatingResult: Boolean? = null,
    var isImageUpdate: Boolean? = null
)