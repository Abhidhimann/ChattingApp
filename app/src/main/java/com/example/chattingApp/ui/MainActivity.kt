package com.example.chattingApp.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.chattingApp.ui.screens.Screen
import com.example.chattingApp.ui.theme.ChattingAppTheme
import com.example.chattingApp.utils.classTag
import com.example.chattingApp.viewModel.UserAuthStateViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        enableEdgeToEdge()
        setContent {
            ChattingAppTheme {
                ChattingApp()
            }
        }
    }
}

@Composable
fun ChattingApp() {
    val bottomNavItems = BottomNavItem.entries.toTypedArray()
    var showBottomNavBar by remember { mutableStateOf(true) }
    val userAuthStateViewModel: UserAuthStateViewModel = hiltViewModel<UserAuthStateViewModel>()
    val navController = rememberNavController()
    val userAuthState = userAuthStateViewModel.authState


    LaunchedEffect(userAuthState) {
        var destination = if (userAuthState == true) {
            Screen.Profile.route
        } else if (userAuthState == false) {
            Screen.SignIn.route
        } else {
            Screen.StartUp.route
        }
        if (navController.currentDestination?.route != Screen.SignIn.route) {
            Log.i(classTag(), "navigating to $destination")
            navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        showBottomNavBar =
            BottomNavItem.entries.toTypedArray().map { it.route }.contains(destination.route)
    }
    Scaffold(
        bottomBar = {
            // todo will remove it from here
            if (showBottomNavBar) {
                BottomNavigationBar(
                    bottomNavItems = bottomNavItems,
                    navController = navController
                )
            }
        }
    ) {
        Surface(Modifier.padding(it)) {
            NavigationHost(navController = navController, Screen.StartUp.route)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChattingApp()
}



