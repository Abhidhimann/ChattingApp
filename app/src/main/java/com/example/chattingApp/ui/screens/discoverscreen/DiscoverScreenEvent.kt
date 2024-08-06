package com.example.chattingApp.ui.screens.discoverscreen

import com.example.chattingApp.domain.model.UserProfile

sealed class DiscoverScreenEvent {
    data class ConnectToUser(val user: UserProfile) : DiscoverScreenEvent()

    // for now not using it, kind of redundant. But could use again in future. So not removing it.
    data class RemoveConnectionRequest(val user: UserProfile) : DiscoverScreenEvent()
    data object ObserveUsers : DiscoverScreenEvent()
    data class OtherUserProfileClicked(val userId: String) : DiscoverScreenEvent()
    data object ResetRequestStatus: DiscoverScreenEvent()
}