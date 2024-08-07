package com.example.chattingApp.presentation.ui.screens.requestscreen

import com.example.chattingApp.domain.model.UserSummary


data class RequestScreenState (
    var requestedUsers: List<UserSummary> = emptyList(),
    var isLoading: Boolean = false,
    var isRequestAccepted: Boolean? = null,
    var isRequestDenied: Boolean? = null,
)