package com.example.chattingApp.utils

object Validation {
    fun validatePassword(password: String): Pair<Boolean, String> {
        var error = ""
        val minLength = 8
        val maxLength = 20
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasNotSpace = !password.contains(" ")
        val hasSpecialChar = password.any { "!@#$%^&*()-_=+[{]}|;:'\",<.>/?".contains(it) }

        if (password.length < minLength) {
            error = "Password minimum length should be $minLength characters."
        } else if (password.length > maxLength) {
            error = "Password maximum length should be $maxLength characters."
        } else if (!hasUppercase) {
            error = "Password must contain at least one uppercase letter."
        } else if (!hasLowercase) {
            error = "Password must contain at least one lowercase letter."
        } else if (!hasDigit) {
            error = "Password must contain at least one digit."
        } else if (!hasSpecialChar) {
            error = "Password must contain at least one special character."
        } else if (!hasNotSpace) {
            error = "Password must not contains space"
        }

        val isInvalid = error.isNotEmpty()
        return Pair(isInvalid, error)
    }

    fun validateEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return !email.matches(emailPattern.toRegex())
    }

    fun validateName(name: String): Pair<Boolean, String> {
        var error = ""
        val isInvalid = name.length > 16
        if (isInvalid){
            error = "Name should be less than 16 characters."
        }
        return Pair(isInvalid, error)
    }
}