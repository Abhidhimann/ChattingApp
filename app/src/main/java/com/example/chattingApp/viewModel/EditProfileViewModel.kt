package com.example.chattingApp.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.data.repository.UserServiceRepositoryImpl
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.ui.screens.editprofilescreen.EditProfileScreenEvent
import com.example.chattingApp.ui.screens.editprofilescreen.EditProfileScreenState
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserServiceRepositoryImpl
) : ViewModel() {

    var state by mutableStateOf(EditProfileScreenState())
        private set

    private var userProfile: UserProfile? = null


    fun onEvent(event: EditProfileScreenEvent) {
        when (event) {
            is EditProfileScreenEvent.FetchSelfProfile -> {
                fetchSelfProfile()
            }

            is EditProfileScreenEvent.UpdateProfileDetails -> {
                Log.i(tempTag(), "new user is ${event.userProfile}")
                updateProfileDetails(event.userProfile)
            }

            is EditProfileScreenEvent.UpdateUserPic -> {
                Log.i(tempTag(), "new user pic is ${event.imageUri}")
                uploadAndUpdateUserPic(event.imageUri)
            }

            else -> Unit
        }
    }

    private fun fetchSelfProfile() = viewModelScope.launch {
        userProfile = repository.getSelfProfileDetails()
        state = state.copy(userProfile = userProfile)
    }

    private fun updateProfileDetails(userProfile: UserProfile) {
        if (this.userProfile == userProfile) {
            state = state.copy(updatingResult = true)
        }
        viewModelScope.launch {
            Log.i(tempTag(), "updating user start")
            val result = repository.updateUserProfile(userProfile)
            state = state.copy(updatingResult = result == 1)
        }
    }

    private fun uploadAndUpdateUserPic(imageUri: Uri) {
        viewModelScope.launch {
            val result = repository.uploadUserPic(imageUri)
                ?: // show error
                return@launch
            val updatedUserProfile = state.userProfile
            updatedUserProfile?.profileImageUrl = result
            state = state.copy(userProfile = updatedUserProfile)
        }
    }
}