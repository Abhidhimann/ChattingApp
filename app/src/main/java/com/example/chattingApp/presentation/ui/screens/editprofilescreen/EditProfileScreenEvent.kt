package com.example.chattingApp.presentation.ui.screens.editprofilescreen

import android.net.Uri
import com.example.chattingApp.domain.model.UserProfile

sealed class EditProfileScreenEvent {
    data class UpdateProfileDetails(val userProfile: UserProfile): EditProfileScreenEvent()
    data object FetchSelfProfile : EditProfileScreenEvent()
    data object CancelOrBack : EditProfileScreenEvent()
    data class UpdateUserPic(val imageUri: Uri): EditProfileScreenEvent()
}
