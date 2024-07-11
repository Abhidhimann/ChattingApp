package com.example.chattingApp.ui.screens.requestscreen

import com.example.chattingApp.domain.model.UserProfile


data class RequestScreenState (
    var requestedUsers: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false
)