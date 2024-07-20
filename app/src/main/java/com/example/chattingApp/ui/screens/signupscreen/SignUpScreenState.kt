package com.example.chattingApp.ui.screens.signupscreen

data class SignUpScreenState (
    val isLoading: Boolean = false,
    val isSignUpSuccess: Boolean? = null,
    val errorMessage: String = ""
)