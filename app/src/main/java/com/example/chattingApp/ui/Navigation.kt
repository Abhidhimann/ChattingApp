package com.example.chattingApp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chattingApp.ui.screens.ChatListScreen
import com.example.chattingApp.ui.screens.chatscreen.ChatScreen
import com.example.chattingApp.ui.screens.discoverScreen.DiscoverPeopleScreen
import com.example.chattingApp.ui.screens.editprofilescreen.EditProfileScreenRoot
import com.example.chattingApp.ui.screens.profilescreen.ProfileScreenRoot

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.CHAT.route) {
        composable(route = BottomNavItem.CHAT.route) { ChatListScreen(navController)}
        composable(route = BottomNavItem.SEARCH.route) { DiscoverPeopleScreen() }
        composable(route = BottomNavItem.PROFILE.route) { ProfileScreenRoot(navController ) }
        composable(route = "chatScreen") { ChatScreen() }
        composable(route = "editProfileScreen") { EditProfileScreenRoot(navController)}
    }
}