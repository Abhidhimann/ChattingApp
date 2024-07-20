package com.example.chattingApp.ui.screens.signin

data class SignInScreenState (
    val isLoading: Boolean = false,
    val isSingInSuccess: Boolean? = null,
    val errorMessage: String = ""
)