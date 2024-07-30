package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.ui.screens.discoverscreen.DiscoverScreenEvent
import com.example.chattingApp.ui.screens.discoverscreen.DiscoverScreenState
import com.example.chattingApp.utils.ResultResponse
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

//    init {
//        isUserProfileExists()
//    }

    fun onEvent(event: DiscoverScreenEvent) {
        when (event) {
            is DiscoverScreenEvent.ObserveUsers -> observeNonConnectedUsers()

            is DiscoverScreenEvent.ConnectToUser -> {
                sendConnectionRequest(event.user)
            }

            is DiscoverScreenEvent.RemoveConnectionRequest -> {
                removeConnectionRequest(event.user)
            }

            is DiscoverScreenEvent.ResetRequestStatus -> {
                state = state.copy(isRequestSuccess = null)
            }

            else -> {}
        }
    }

    private fun sendConnectionRequest(toUser: UserProfile) = viewModelScope.launch {
        val sendRequest = repository.sendConnectionRequest(toUser.toUserSummary())
        when (sendRequest) {
            is ResultResponse.Failed -> {

            }

            is ResultResponse.Success -> {
                val updatedUsers = state.users.toMutableList()
                Log.i(tempTag(), "updated user $updatedUsers and $toUser")
                val existingUserIndex = updatedUsers.indexOfFirst { it.userId == toUser.userId }
                Log.i(tempTag(), "index $existingUserIndex")
                if (existingUserIndex != -1) {
                    // either remove or do this
//            toUser.relation = UserRelation.ALREADY_REQUESTED
//            updatedUsers[existingUserIndex] = toUser
                    updatedUsers.remove(toUser)
                }
                Log.i(tempTag(), "after $updatedUsers")
                state = state.copy(users = updatedUsers, isRequestSuccess = true)
                Log.i(tempTag(), "state after update: $state")
            }
        }
    }

    private fun removeConnectionRequest(toUser: UserProfile) = viewModelScope.launch {
        repository.removeConnectionRequestByRequestedUser(toUser.userId)
    }

    private fun observeNonConnectedUsers() = viewModelScope.launch {
        repository.observeNonConnectedUsers().catch { e ->
            Log.e(classTag(), "Error observing users", e)
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
}