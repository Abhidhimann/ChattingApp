package com.example.chattingApp.ui.screens.discoverScreen

sealed class DiscoverScreenEvent {
    data object FindNewUser : DiscoverScreenEvent()
    data class ConnectToUser(val userId: String): DiscoverScreenEvent()
    data object ObserveUsers: DiscoverScreenEvent()
    data object ChatAnonymously: DiscoverScreenEvent()
    data object MakeProfile: DiscoverScreenEvent()
    data object CheckUserProfile: DiscoverScreenEvent()
    data class OtherUserProfileClicked(val userId: String): DiscoverScreenEvent()

    data class UpdateUserReadyToChatStatus(val value: Boolean): DiscoverScreenEvent()
    data object temp: DiscoverScreenEvent()
}