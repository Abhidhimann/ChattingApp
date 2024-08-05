package com.example.chattingApp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattingApp.domain.repository.AuthRepository
import com.example.chattingApp.domain.repository.UserServiceRepository
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
    private val authRepository: AuthRepository,
    private val userServiceRepository: UserServiceRepository,
) : ViewModel() {

    var state by mutableStateOf(SignInScreenState())
        private set

    fun onEvent(event: SignInScreenEvent) {
        when (event) {
            is SignInScreenEvent.SignInByEmailAndPassword -> {
                singInByEmailAndPassword(event.email, event.password)
            }

            is SignInScreenEvent.GoogleSso -> {
                signInWithGoogleSso()
            }

            else -> Unit
        }
    }

    private fun singInByEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        state = state.copy(isLoading = true)
        when (val result = authRepository.signInUsingEmailAndPassword(email, password)) {
            is ResultResponse.Success -> {
                Log.i(classTag(), "Sign in successful ${result.data}")
                doSignIn()
            }

            is ResultResponse.Failed -> {
                when (result.exception) {
                    is SignInException.EmailNotVerifiedException -> {
                        state = state.copy(
                            isLoading = false,
                            isSingInSuccess = false,
                            errorMessage = "Your email address is not yet verified. Please check your inbox for the verification email."
                        )
                    }

                    is SignInException.GeneralException -> {
                        state = state.copy(
                            isLoading = false,
                            isSingInSuccess = false,
                            errorMessage = "Email id or password is incorrect."
                        )
                    }
                }
            }
        }
    }

    private fun signInWithGoogleSso() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        when (val result = authRepository.signInWithGoogleSso()) {
            is ResultResponse.Success -> {
                Log.i(classTag(), "Sign in successful ${result.data}")
                doSignIn()
            }

            is ResultResponse.Failed -> {
                Log.e(classTag(), "error in google sso ${result.exception}")
                state = state.copy(
                    isLoading = false,
                    isSingInSuccess = false,
                    errorMessage = "Some error occurred. Please try again later or sign up using email address."
                )
            }
        }
    }

    private fun doSignIn() = viewModelScope.launch {
        when (val result2 = userServiceRepository.updateUserTokenFromLocal()) {
            is ResultResponse.Success -> {
                state = state.copy(isSingInSuccess = true, isLoading = false)
            }

            is ResultResponse.Failed -> {
                Log.e(classTag(), "error in token updating ${result2.exception}")
                state = state.copy(
                    isLoading = false,
                    isSingInSuccess = false,
                    errorMessage = "Some error occurred. Please try again later."
                )
            }
        }
    }
}