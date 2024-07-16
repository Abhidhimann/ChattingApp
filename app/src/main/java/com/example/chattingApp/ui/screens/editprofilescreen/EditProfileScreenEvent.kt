package com.example.chattingApp.ui.screens.editprofilescreen

import com.example.chattingApp.domain.model.UserProfile

sealed class EditProfileScreenEvent {
    data class SaveProfileDetails(val userProfile: UserProfile): EditProfileScreenEvent()
    data object Cancel : EditProfileScreenEvent()
}
