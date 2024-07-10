package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenEvent
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenState
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
                if (event.userId == null || event.userId == "{userId}") {
                    fetchSelfProfile()
                } else {
                    fetchUserProfile(userId = event.userId)
                }
            }

            else -> {}
        }
    }

    private fun fetchUserProfile(userId: String) = viewModelScope.launch {
        val userProfile = repository.getUserProfileDetails(userId)
        state = state.copy(userProfile = userProfile)
    }

    private fun fetchSelfProfile() = viewModelScope.launch {
        val userProfile = repository.getSelfProfileDetails()
        state = state.copy(userProfile = userProfile)
    }

}