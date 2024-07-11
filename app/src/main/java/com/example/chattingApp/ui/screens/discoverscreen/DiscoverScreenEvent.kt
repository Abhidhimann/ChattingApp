package com.example.chattingApp.ui.screens.discoverscreen

import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserSummary

sealed class DiscoverScreenEvent {
    data object FindNewUser : DiscoverScreenEvent()
    data class ConnectToUser(val user: UserProfile): DiscoverScreenEvent()
    data class RemoveConnectionRequest(val user: UserProfile): DiscoverScreenEvent()
    data object ObserveUsers: DiscoverScreenEvent()
    data object ChatAnonymously: DiscoverScreenEvent()
    data object MakeProfile: DiscoverScreenEvent()
    data object CheckUserProfile: DiscoverScreenEvent()
    data class OtherUserProfileClicked(val userId: String): DiscoverScreenEvent()

    data class UpdateUserReadyToChatStatus(val value: Boolean): DiscoverScreenEvent()
    data object temp: DiscoverScreenEvent()
}