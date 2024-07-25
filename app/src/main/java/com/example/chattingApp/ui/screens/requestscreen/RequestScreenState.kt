package com.example.chattingApp.ui.screens.requestscreen

import com.example.chattingApp.domain.model.UserSummary


data class RequestScreenState (
    var requestedUsers: List<UserSummary> = emptyList(),
    val isLoading: Boolean = false
)