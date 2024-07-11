package com.example.chattingApp.ui.screens.requestscreen

sealed class RequestScreenEvent {
    data object ObserveRequestUsers : RequestScreenEvent()
    data class AcceptRequest(val toUserId: String): RequestScreenEvent()
    data class RejectRequest(val toUserId: String): RequestScreenEvent()
    data class RequestedUserProfileClicked(val toUserId: String): RequestScreenEvent()
}