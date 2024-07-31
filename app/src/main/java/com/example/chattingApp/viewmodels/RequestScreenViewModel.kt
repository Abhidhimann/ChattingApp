package com.example.chattingApp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.domain.repository.UserServiceRepository
import com.example.chattingApp.ui.screens.requestscreen.RequestScreenEvent
import com.example.chattingApp.ui.screens.requestscreen.RequestScreenState
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.utils.tempTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestScreenViewModel @Inject constructor(
    private val repository: UserServiceRepository,
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

            is RequestScreenEvent.ResetRequestStates -> {
                state = state.copy(isRequestDenied = null, isRequestAccepted = null)
            }

            else -> {}
        }
    }

    private fun rejectConnectionRequest(userId: String) = viewModelScope.launch {
        state = when (repository.removeConnectionRequestBySelf(userId)) {
            is ResultResponse.Success -> {
                Log.i(tempTag(), "coming here")
                state.copy(isRequestDenied = true)
            }

            is ResultResponse.Failed -> {
                state.copy(isRequestDenied = false)
            }
        }
    }

    private fun acceptConnectionRequest(userSummary: UserSummary) = viewModelScope.launch {
        state = when (repository.acceptConnectionRequest(userSummary)) {
            is ResultResponse.Success -> {
                state.copy(isRequestAccepted = true)
            }

            is ResultResponse.Failed -> {
                state.copy(isRequestAccepted = false)
            }
        }
    }

    private fun observeIncomingConnectionRequests() = viewModelScope.launch {
        repository.observeIncomingRequests().catch { e ->
            Log.e(classTag(), "Error observing requests", e)
        }.collect { userProfile ->
            if (userProfile == null) {   // i.e. no incoming request
                state = state.copy(requestedUsers = emptyList())
                return@collect
            }
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