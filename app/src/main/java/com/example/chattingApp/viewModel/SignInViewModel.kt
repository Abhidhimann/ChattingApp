package com.example.chattingApp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.ui.screens.signin.SignInScreenEvent
import com.example.chattingApp.ui.screens.signin.SignInScreenState
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.SignInException
import com.example.chattingApp.utils.classTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(SignInScreenState())
        private set

    fun onEvent(event: SignInScreenEvent) {
        when (event) {
            is SignInScreenEvent.SignInByEmailAndPassword -> {
                singInByEmailAndPassword(event.email, event.password)
            }

            else -> Unit
        }
    }

    private fun singInByEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        state = state.copy(isLoading = true)
        when (val result = authRepository.signInUsingEmailAndPassword(email, password)) {
            is ResultResponse.Success -> {
                Log.i(classTag(), "Sign in successful ${result.data}")
                state = state.copy(isSingInSuccess = true, isLoading = false)
            }

            is ResultResponse.Failed -> {
                when(result.exception){
                    is SignInException.EmailNotVerifiedException -> {
                        state = state.copy(isLoading = false, isSingInSuccess = false, errorMessage = "Your email address is not yet verified. Please check your inbox for the verification email.")
                    }
                    is SignInException.GeneralException -> {
                        state = state.copy(isLoading = false, isSingInSuccess = false, errorMessage = "Email id or password is incorrect.")
                    }
                }
            }
        }
    }
}