package com.example.chattingApp.presentation.ui.screens.requestscreen


import com.example.chattingApp.domain.model.UserSummary

sealed class RequestScreenEvent {
    data object ObserveRequestUsers : RequestScreenEvent()
    data class AcceptRequest(val userSummary: UserSummary): RequestScreenEvent()
    data class RejectRequest(val userId: String): RequestScreenEvent()
    data class RequestedUserProfileClicked(val userId: String): RequestScreenEvent()
    data object ResetRequestStates: RequestScreenEvent()
}