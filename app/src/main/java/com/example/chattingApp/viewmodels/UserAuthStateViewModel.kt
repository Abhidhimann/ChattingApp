package com.example.chattingApp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.utils.classTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthStateViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // todo later change it to enum authState
    var authState by mutableStateOf<Boolean?>(null)
        private set

    init {
        getAuthState()
    }

    private fun getAuthState() = viewModelScope.launch {
        authRepository.getAuthState().catch {
            Log.d(classTag(), "Error in getting auth state")
        }.collect {
            authState = it
        }
    }

}