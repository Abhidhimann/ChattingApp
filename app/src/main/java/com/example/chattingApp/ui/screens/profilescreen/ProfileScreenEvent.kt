package com.example.chattingApp.ui.screens.profilescreen

import com.example.chattingApp.domain.model.UserProfile

sealed class ProfileScreenEvent {
    data class EditProfile(val userProfile: UserProfile) : ProfileScreenEvent()
    data class FetchUserProfile(val userId: String?) : ProfileScreenEvent()
}