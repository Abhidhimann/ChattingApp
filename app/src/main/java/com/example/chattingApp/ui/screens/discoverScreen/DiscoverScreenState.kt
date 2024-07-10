package com.example.chattingApp.ui.screens.discoverScreen

import com.example.chattingApp.domain.model.UserProfile

data class DiscoverScreenState (
    var users: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val isUserProfileUpdated: Boolean = true,
    val userReadyToChat: Boolean = true,
    val isUserProfileExists: Boolean = true,
)