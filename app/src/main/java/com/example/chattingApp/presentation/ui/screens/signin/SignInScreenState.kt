package com.example.chattingApp.presentation.ui.screens.signin

data class SignInScreenState (
    val isLoading: Boolean = false,
    val isSingInSuccess: Boolean? = null,
    val errorMessage: String = ""
)