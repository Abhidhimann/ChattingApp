package com.example.chattingApp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chattingApp.ui.screens.ChatListScreen
import com.example.chattingApp.ui.screens.chatscreen.ChatScreen
import com.example.chattingApp.ui.screens.discoverscreen.DiscoverPeopleScreenRoot
import com.example.chattingApp.ui.screens.editprofilescreen.EditProfileScreenRoot
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenRoot
import com.example.chattingApp.ui.screens.requestscreen.RequestScreenRoot

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.CHAT.route) {
        composable(route = BottomNavItem.CHAT.route) { ChatListScreen(navController) }
        composable(route = BottomNavItem.CONNECT.route) { DiscoverPeopleScreenRoot(navController) }
        composable(route = BottomNavItem.REQUEST.route) { RequestScreenRoot() }
        composable(route = BottomNavItem.PROFILE.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreenRoot(navController = navController, userId = userId)
        }
        composable(route = "chatScreen") { ChatScreen() }
        composable(route = "editProfileScreen") { EditProfileScreenRoot(navController) }
    }
}