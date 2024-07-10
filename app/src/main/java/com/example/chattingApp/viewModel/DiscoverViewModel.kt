package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.ui.screens.discoverScreen.DiscoverScreenEvent
import com.example.chattingApp.ui.screens.discoverScreen.DiscoverScreenState
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: UserServiceRepository
) : ViewModel() {

    var state by mutableStateOf(DiscoverScreenState())
        private set

    init {
        isUserProfileExists()
        createUserIfNotExists()
    }

    fun onEvent(event: DiscoverScreenEvent) {
        when (event) {
            is DiscoverScreenEvent.ObserveUsers -> observeNonConnectedUsers()
            is DiscoverScreenEvent.temp -> {}
            is DiscoverScreenEvent.UpdateUserReadyToChatStatus -> {
                updateUserOnlineStatus(
                    event.value
                )
            }

            is DiscoverScreenEvent.ConnectToUser -> {
                sendConnectionRequest(event.userId)
            }

            else -> {}
        }
    }

    private fun sendConnectionRequest(toUserId: String) = viewModelScope.launch {
        repository.sendConnectionRequest(toUserId = toUserId)
    }

    private fun observeNonConnectedUsers() = viewModelScope.launch {
        repository.observeNonConnectedUsers().catch { e ->
            Log.e(classTag(), "Error observing messages", e)
        }.collect { userProfile ->
            Log.i(classTag(), "got user from flow $userProfile")
            val updatedUsers = state.users.toMutableList()
            val existingUserIndex = updatedUsers.indexOfFirst { it.userId == userProfile.userId }

            if (existingUserIndex != -1) {
                updatedUsers[existingUserIndex] = userProfile
            } else {
                updatedUsers.add(userProfile)
            }
            state = state.copy(users = updatedUsers)
        }
    }

    fun createUserIfNotExists() = viewModelScope.launch {
        if (!repository.isUserIdExists()) {
            Log.i(tempTag(), "creating user")
            repository.createUser()
        }
    }

    private fun isUserProfileExists() = viewModelScope.launch {
        val result = repository.isUserProfileExists()
        Log.i(tempTag(), "user profile exists: $result")
        state = state.copy(isUserProfileExists = result)
    }

    fun updateUserOnlineStatus(value: Boolean) =
        viewModelScope.launch {
//            repository.updateUserOnlineStatus(value)
        }

    private fun temp() = viewModelScope.launch {
        repository.temp()
    }

}