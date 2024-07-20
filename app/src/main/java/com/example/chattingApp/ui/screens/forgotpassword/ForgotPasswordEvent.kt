package com.example.chattingApp.ui.screens.forgotpassword

sealed class ForgotPasswordEvent {
    data object Login: ForgotPasswordEvent()
    data class SubmitEmail(val email: String): ForgotPasswordEvent()
}