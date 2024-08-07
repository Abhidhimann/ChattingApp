package com.example.chattingApp.presentation.ui.screens

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object SignIn : Screen("signInScreen")
    data object SignUp : Screen("signUpScreen")
    data object ForgotPassword : Screen("forgotPasswordScreen")
    data object Profile : Screen("profile?userId={userId}", listOf(navArgument("userId") {
        defaultValue = ""
        type = NavType.StringType
    })) {
        fun createRoute(userId: String) = "profile?userId=${userId}"
    }

    data object EditProfile : Screen("editProfileScreen")
    data object ChatList: Screen("chatList")
    data object Discover: Screen("connect")
    data object Requests: Screen("requests")
    data object Chat: Screen("chatScreen/{chatId}", listOf(navArgument("chatId"){
        type = NavType.StringType
    })) {
        fun createRoute(chatId: String) = "chatScreen/${chatId}"
    }

    data object StartUp: Screen("startUpLoadingScreen")
}