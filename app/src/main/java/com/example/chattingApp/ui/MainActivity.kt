package com.example.chattingApp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.chattingApp.theme.ChattingAppTheme
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

    Scaffold(
        bottomBar = {
            BottomNavigationBar(tabBarItems = bottomNavItems, navController = navController)
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



