package com.example.chattingApp.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chattingApp.presentation.ui.screens.Screen
import com.example.chattingApp.presentation.ui.screens.splashscreen.StartUpLoading
import com.example.chattingApp.presentation.ui.screens.chatlistscreen.ChatListScreenRoot
import com.example.chattingApp.presentation.ui.screens.chatscreen.ChatScreenRoot
import com.example.chattingApp.presentation.ui.screens.discoverscreen.DiscoverPeopleScreenRoot
import com.example.chattingApp.presentation.ui.screens.editprofilescreen.EditProfileScreenRoot
import com.example.chattingApp.presentation.ui.screens.forgotpassword.ForgotPasswordScreenRoot
import com.example.chattingApp.presentation.ui.screens.profilescreen.ProfileScreenRoot
import com.example.chattingApp.presentation.ui.screens.requestscreen.RequestScreenRoot
import com.example.chattingApp.presentation.ui.screens.signin.SignInScreenRoot
import com.example.chattingApp.presentation.ui.screens.signupscreen.SignUpScreenRoot

@Composable
fun NavigationHost(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable(route = Screen.ChatList.route) { ChatListScreenRoot(navController = navController) }
        composable(route = Screen.Discover.route) { DiscoverPeopleScreenRoot(navController) }
        composable(route = Screen.Requests.route) { RequestScreenRoot(navController) }
        composable(
            route = Screen.Profile.route,
            arguments = Screen.Profile.navArguments
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            userId?.let {
                ProfileScreenRoot(navController = navController, userId = it)
            }
        }
        composable(
            route = Screen.Chat.route,
            arguments = Screen.Chat.navArguments
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")
            chatId?.let {
                ChatScreenRoot(it, navController)
            }
        }
        composable(route = Screen.EditProfile.route) { EditProfileScreenRoot(navController) }
        composable(route = Screen.SignIn.route) { SignInScreenRoot(navController) }
        composable(route = Screen.SignUp.route) { SignUpScreenRoot(navController) }
        composable(route = Screen.ForgotPassword.route) { ForgotPasswordScreenRoot(navController) }
        composable(route = Screen.StartUp.route) {
            StartUpLoading()
        }
    }
}

