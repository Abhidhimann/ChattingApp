package com.example.chattingApp.presentation.ui.screens.signin

sealed class SignInScreenEvent {
    data object RegisterUser : SignInScreenEvent()
    data class SignInByEmailAndPassword(val email: String, val password: String) :
        SignInScreenEvent()

    data object ForgotPassword: SignInScreenEvent()
    data object AfterSignIn: SignInScreenEvent()
    data object GoogleSso : SignInScreenEvent()
}