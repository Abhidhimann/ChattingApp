package com.example.chattingApp.ui.screens.signin

sealed class SingInScreenEvent {
    data object RegisterUser : SingInScreenEvent()
    data object SignInByEmail: SingInScreenEvent()
    data object GoogleSso: SingInScreenEvent()
}