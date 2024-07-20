package com.example.chattingApp.ui.screens.profilescreen

import com.example.chattingApp.domain.model.UserProfile

data class ProfileScreenState (
    var isSelfUser: Boolean = true,
    var userProfile: UserProfile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String = "",
    val isLogoutSuccess: Boolean? = null
)