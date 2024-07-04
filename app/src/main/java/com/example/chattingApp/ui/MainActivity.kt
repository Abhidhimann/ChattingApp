package com.example.chattingApp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.chattingApp.ui.theme.ChattingAppTheme
import com.example.chattingApp.ui.screens.BottomNavItem
import com.example.chattingApp.ui.screens.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChattingAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = BottomNavItem.entries.toTypedArray()
    var showBottomNavBar by remember { mutableStateOf(true) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        showBottomNavBar =
            BottomNavItem.entries.toTypedArray().map { it.route }.contains(destination.route)
    }
    Scaffold(
        bottomBar = {
            if (showBottomNavBar) {
                BottomNavigationBar(bottomNavItems = bottomNavItems, navController = navController)
            }
        }
    ) {
        NavigationHost(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}



