package com.example.chattingApp.presentation.ui.screens.discoverscreen

import com.example.chattingApp.domain.model.UserProfile

data class DiscoverScreenState (
    var users: List<UserProfile> = emptyList(),
    var isLoading: Boolean = false,
    var isRequestSuccess: Boolean? = null
)