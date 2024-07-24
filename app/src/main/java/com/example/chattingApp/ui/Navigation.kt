package com.example.chattingApp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chattingApp.ui.screens.StartLoadingScreen
import com.example.chattingApp.ui.screens.chatlistscreen.ChatListScreenRoot
import com.example.chattingApp.ui.screens.chatscreen.ChatScreenRoot
import com.example.chattingApp.ui.screens.discoverscreen.DiscoverPeopleScreenRoot
import com.example.chattingApp.ui.screens.editprofilescreen.EditProfileScreenRoot
import com.example.chattingApp.ui.screens.forgotpassword.ForgotPasswordScreenRoot
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenRoot
import com.example.chattingApp.ui.screens.requestscreen.RequestScreenRoot
import com.example.chattingApp.ui.screens.signin.SignInScreenRoot
import com.example.chattingApp.ui.screens.signupscreen.SignUpScreenRoot

@Composable
fun NavigationHost(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable(route = BottomNavItem.CHAT_LIST.route) { ChatListScreenRoot(navController = navController) }
        composable(route = BottomNavItem.CONNECT.route) { DiscoverPeopleScreenRoot(navController) }
        composable(route = BottomNavItem.REQUEST.route) { RequestScreenRoot(navController) }
        composable(route = BottomNavItem.PROFILE.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreenRoot(navController = navController, userId = userId)
        }

        composable(route = "chatScreen/{chatId}") { backStackEntry ->
            val chatId: String = backStackEntry.arguments!!.getString("chatId")!!
            ChatScreenRoot(chatId, navController)
        }
        composable(route = "editProfileScreen") { EditProfileScreenRoot(navController) }
        composable(route = "signInScreen") { SignInScreenRoot(navController) }
        composable(route = "signUpScreen") { SignUpScreenRoot(navController) }
        composable(route = "forgotPasswordScreen") { ForgotPasswordScreenRoot(navController) }
        composable(route = "startLoadingScreen") { StartLoadingScreen() }
    }
}

