package com.example.chattingApp.presentation.ui.screens.forgotpassword

sealed class ForgotPasswordEvent {
    data object OnBackPressed: ForgotPasswordEvent()
    data class SubmitEmail(val email: String): ForgotPasswordEvent()
}