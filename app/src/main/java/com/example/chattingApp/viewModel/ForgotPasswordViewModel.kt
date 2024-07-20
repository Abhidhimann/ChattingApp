package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.ui.screens.forgotpassword.ForgotPasswordEvent
import com.example.chattingApp.ui.screens.forgotpassword.ForgotPasswordState
import com.example.chattingApp.utils.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(ForgotPasswordState())
        private set

    fun onEvent(event: ForgotPasswordEvent){
        when(event){
            is ForgotPasswordEvent.SubmitEmail -> {
                sendPasswordResetEmail(event.email)
            }
            else -> Unit
        }
    }

    private fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        when(val result = authRepository.sendPasswordResetEmail(email)){
            is ResultResponse.Success -> {
                Log.i("ABHITAG", "password reset mail send successfully")
                state = state.copy(isSendingPasswordResetSuccess = true)
            }

            is ResultResponse.Failed -> {
                Log.i("ABHITAG", "password reset mail failed ${result.exception}")
                state = state.copy(isSendingPasswordResetSuccess = false, errorMessage = "Some error occurred. Try again later.")
            }
        }
    }
}