package com.example.chattingApp.ui.screens.editprofilescreen

import com.example.chattingApp.domain.model.UserProfile

sealed class EditProfileScreenEvent {
    data class UpdateProfileDetails(val userProfile: UserProfile): EditProfileScreenEvent()
    data object FetchSelfProfile : EditProfileScreenEvent()
    data object CancelOrBack : EditProfileScreenEvent()
}
