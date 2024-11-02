package com.example.chattingApp.utils

sealed class SignInException(message: String) : Exception(message) {
    class EmailNotVerifiedException : SignInException("Email is not verified.")
    class GeneralException(message: String) : SignInException(message)
}

sealed class SignUpException(message: String) : Exception(message) {
    class UserAlreadyExists : SignUpException("User already exits with same email address.")
    class GeneralException(message: String) : SignUpException(message)
}

sealed class AIChatBotException(message: String) : Exception(message) {
    class LimitExceedException : AIChatBotException("Limit exceeded please start new conversation.")
    class GeneralException(message: String): AIChatBotException(message)
}