package com.example.chattingApp.presentation.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.presentation.ui.screens.editprofilescreen.EditProfileScreenEvent
import com.example.chattingApp.presentation.ui.screens.editprofilescreen.EditProfileScreenState
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserServiceRepository
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
                uploadUserPic(event.imageUri)
            }

            else -> Unit
        }
    }

    private fun fetchSelfProfile() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        state = when (val userProfileResult = repository.getSelfProfileDetails()) {
            is ResultResponse.Success -> {
                state.copy(userProfile = userProfileResult.data, isLoading = false)
            }

            is ResultResponse.Failed -> {
                state.copy(isLoading = false)
            }
        }
    }

    private fun updateProfileDetails(userProfile: UserProfile) {
        if (this.userProfile == userProfile) {
            state = state.copy(updatingResult = true)
        }
        viewModelScope.launch {
            Log.i(tempTag(), "updating user start")
            state = state.copy(isLoading = true)
            state = when (val userProfileResult = repository.updateUserProfile(userProfile)) {
                is ResultResponse.Success -> {
                    state.copy(updatingResult = true, isLoading = false)
                }

                is ResultResponse.Failed -> {
                    state.copy(updatingResult = false, isLoading = false)
                }
            }
        }
    }

    private fun uploadUserPic(imageUri: Uri) {
        viewModelScope.launch {
            state = state.copy(isImageUploading = true)
            state = when (val result = repository.uploadUserPic(imageUri)) {
                is ResultResponse.Success -> {
                    Log.i(classTag(), "Image upload successful ${result.data}")
                    val updatedUserProfile = state.userProfile
                    Log.i(classTag(), "previous user $updatedUserProfile")
                    updatedUserProfile?.profileImageUrl = result.data
                    Log.i(classTag(), "updated user $updatedUserProfile")
                    state.copy(imageUploadingResult = true, isImageUploading = false)
                }

                is ResultResponse.Failed -> {
                    Log.i(classTag(), "Image upload failed ${result.exception}")
                    state.copy(isImageUploading = false, imageUploadingResult = false)
                }
            }
        }
    }
}