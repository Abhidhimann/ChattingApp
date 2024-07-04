package com.example.chattingApp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chattingApp.ui.screens.BottomNavItem
import com.example.chattingApp.ui.screens.ChatListScreen
import com.example.chattingApp.ui.screens.ChatScreen
import com.example.chattingApp.ui.screens.ChatScreenPreview
import com.example.chattingApp.ui.screens.DiscoverPeopleScreen
import com.example.chattingApp.ui.screens.ProfileScreen

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.CHAT.route) {
        composable(route = BottomNavItem.CHAT.route) { ChatListScreen(navController)}
        composable(route = BottomNavItem.SEARCH.route) { DiscoverPeopleScreen() }
        composable(route = BottomNavItem.PROFILE.route) { ProfileScreen(navController) }
        composable(route = "chatScreen") { ChatScreen() }
    }
}