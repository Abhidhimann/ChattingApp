package com.example.chattingApp.presentation.ui.screens.forgotpassword

data class ForgotPasswordState (
    val isLoading: Boolean = false,
    val isSendingPasswordResetSuccess: Boolean? = null,
    val errorMessage: String = ""
)