package com.example.chattingApp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenEvent
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenState
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repository: UserServiceRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileScreenState())
        private set

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.FetchUserProfile -> {
                Log.i(tempTag(), "coming here ${event.userId}")
                // todo navigation needs updation, I guess in new library we don't need that
                // plus event.userId could not be null default value will be userId always
                if (event.userId.isEmpty()) {
                    fetchSelfProfile()
                } else {
                    fetchUserProfile(userId = event.userId)
                }
            }

            is ProfileScreenEvent.LogOut -> {
                logOut()
            }

            else -> {}
        }
    }

    private fun fetchUserProfile(userId: String) = viewModelScope.launch {
        state = state.copy(isLoading = true)
        state = when (val userProfileResult = repository.getUserProfileDetails(userId)) {
            is ResultResponse.Success -> {
                state.copy(userProfile = userProfileResult.data, isLoading = false)
            }

            is ResultResponse.Failed -> {
                state.copy(isLoading = false, errorMessage = "")
            }
        }
    }

    private fun fetchSelfProfile() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        state = when (val userProfileResult = repository.getSelfProfileDetails()) {
            is ResultResponse.Success -> {
                state.copy(userProfile = userProfileResult.data, isLoading = false)
            }

            is ResultResponse.Failed -> {
                state.copy(isLoading = false, errorMessage = "")
            }
        }
    }

    private fun logOut() = viewModelScope.launch {
        when (val result = repository.logOut()) {
            is ResultResponse.Success -> {
                state = state.copy(isLogoutSuccess = true)
            }

            is ResultResponse.Failed -> {
                Log.i(classTag(), "logout failed with ${result.exception}")
                state = state.copy(
                    isLogoutSuccess = false,
                    errorMessage = "Some error occurred. Please try again later."
                )
            }
        }
    }

}