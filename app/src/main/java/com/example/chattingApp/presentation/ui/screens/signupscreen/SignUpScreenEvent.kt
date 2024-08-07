package com.example.chattingApp.presentation.ui.screens.signupscreen


sealed class SignUpScreenEvent {
    data class SignUp(val email: String, val password: String, val name: String): SignUpScreenEvent()
    data object Login: SignUpScreenEvent()
    data object OnBackPressed: SignUpScreenEvent()
    data object ResetSingUpState: SignUpScreenEvent()
}