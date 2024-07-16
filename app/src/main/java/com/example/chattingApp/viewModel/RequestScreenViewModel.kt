package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.data.repository.UserServiceRepositoryImpl
import com.example.chattingApp.ui.screens.requestscreen.RequestScreenEvent
import com.example.chattingApp.ui.screens.requestscreen.RequestScreenState
import com.example.chattingApp.utils.classTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestScreenViewModel @Inject constructor(
    private val repository: UserServiceRepositoryImpl,
) : ViewModel() {

    var state by mutableStateOf(RequestScreenState())
        private set

    fun onEvent(event: RequestScreenEvent) {
        when (event) {
            is RequestScreenEvent.ObserveRequestUsers -> {
                observeIncomingConnectionRequests()
            }
            is RequestScreenEvent.RejectRequest -> {
                rejectConnectionRequest(event.userId)
            }
            is RequestScreenEvent.AcceptRequest -> {
                acceptConnectionRequest(event.userSummary)
            }
            else -> {}
        }
    }

    private fun rejectConnectionRequest(userId: String) = viewModelScope.launch {
        repository.removeConnectionRequestBySelf(userId)
    }

    private fun acceptConnectionRequest(userSummary: UserSummary) = viewModelScope.launch {
        repository.acceptConnectionRequest(userSummary)
    }

    private fun observeIncomingConnectionRequests() = viewModelScope.launch {
        repository.observeIncomingRequests().catch { e ->
            Log.e(classTag(), "Error observing requests", e)
        }.collect { userProfile ->
            Log.i(classTag(), "got user profile from flow $userProfile")
            val updatedRequests = state.requestedUsers.toMutableList()
            val existingUserIndex = updatedRequests.indexOfFirst { it.userId == userProfile.userId }

            if (existingUserIndex != -1) {
                updatedRequests[existingUserIndex] = userProfile
            } else {
                updatedRequests.add(userProfile)
            }
            state = state.copy(requestedUsers = updatedRequests)
        }
    }
}