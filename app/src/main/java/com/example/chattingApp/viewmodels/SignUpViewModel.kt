package com.example.chattingApp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.ui.screens.signupscreen.SignUpScreenEvent
import com.example.chattingApp.ui.screens.signupscreen.SignUpScreenState
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.SignUpException
import com.example.chattingApp.utils.classTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(SignUpScreenState())
        private set

    fun onEvent(event: SignUpScreenEvent) {
        when (event) {
            is SignUpScreenEvent.SignUp -> {
                signUp(event.email, event.password, event.name)
            }

            is SignUpScreenEvent.ResetSingUpState -> {
                state = state.copy(isSignUpSuccess = null)
            }

            else -> Unit
        }
    }

    private fun signUp(email: String, password: String, name: String) = viewModelScope.launch {
        state = state.copy(isLoading = true)
        when (val result = authRepository.signUpUsingEmailAndPassword(email, password, name)) {
            is ResultResponse.Success -> {
                // showToast
                Log.i(classTag(), "user successfully created ${result.data}")
                state = state.copy(isSignUpSuccess = true, isLoading = false)
            }

            is ResultResponse.Failed -> {
                Log.i(classTag(), "error in creating user")
                when(result.exception){
                    is SignUpException.UserAlreadyExists -> {
                        state = state.copy(isLoading = false, isSignUpSuccess = false, errorMessage = "User already exits with same email address.")
                    }
                    is SignUpException.GeneralException -> {
                        state = state.copy(isLoading = false, isSignUpSuccess = false, errorMessage = "Some error occurred. Please try again later.")
                    }
                }
            }
        }
    }
}