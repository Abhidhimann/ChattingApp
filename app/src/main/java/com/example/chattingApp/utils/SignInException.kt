package com.example.chattingApp.utils

sealed class SignInException(message: String): Exception(message) {
    class EmailNotVerifiedException : SignInException("Email is not verified.")
    class GeneralException(message: String) : SignInException(message)
}

sealed class SignUpException(message: String): Exception(message) {
    class UserAlreadyExists : SignUpException("User already exits with same email address.")
    class GeneralException(message: String) : SignUpException(message)
}