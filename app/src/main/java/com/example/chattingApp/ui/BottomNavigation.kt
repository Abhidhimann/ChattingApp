package com.example.chattingApp.ui

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chattingApp.ui.screens.Screen

enum class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val title: String
) {
    CHAT_LIST(Screen.ChatList.route, Icons.Default.Email, Icons.Default.Email, "Chat"),
    CONNECT(Screen.Discover.route, Icons.Default.Search, Icons.Default.Search, "Connect"),
    REQUEST(Screen.Requests.route, Icons.Default.List, Icons.Default.List, "Requests"),
    PROFILE(Screen.Profile.route, Icons.Default.Person, Icons.Default.Person, "Profile");
}

@Composable
fun BottomNavigationBar(bottomNavItems: Array<BottomNavItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        selectedTabIndex =
            bottomNavItems.indexOfFirst { it.route == currentRoute }.takeIf { it != -1 } ?: 0
        Log.i("ABHITAG", "Select tab index is $selectedTabIndex")
    }

    NavigationBar() {
        bottomNavItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    val currentIndex = selectedTabIndex
                    selectedTabIndex = index
                    if (currentIndex != index) navController.navigate(tabBarItem.route)
                },
                icon = {
                    NavigationItem(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                    )
                },
                label = { Text(tabBarItem.title) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationItem(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            },
            contentDescription = title
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

