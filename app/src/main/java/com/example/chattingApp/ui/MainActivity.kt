package com.example.chattingApp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chattingApp.ui.theme.ChattingAppTheme
import com.example.chattingApp.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest

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
    val navController = rememberNavController()
    val bottomNavItems = BottomNavItem.entries.toTypedArray()
    var showBottomNavBar by remember { mutableStateOf(true) }
    // todo find out main app should have view model or not ( i guess should not)
    val authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
//    val isAuthenticate by authViewModel.isAuthenticate.collectAsState()
    val startDestination = "signInScreen"
    val isAuthenticate = authViewModel.authState
    // todo will optimize it
    if (navController.currentDestination != null && navController.currentDestination?.route != "signInScreen") {
        val newDes = if (isAuthenticate == true) {
            BottomNavItem.CHAT_LIST.route
        } else if (isAuthenticate == null) {
            "startLoadingScreen"
        } else {
            "signInScreen"
        }
        navController.navigate(newDes)
    }


    navController.addOnDestinationChangedListener { _, destination, _ ->
        showBottomNavBar =
            BottomNavItem.entries.toTypedArray().map { it.route }.contains(destination.route)
    }
    // todo will remove it from here
    Scaffold(
        bottomBar = {
            if (showBottomNavBar) {
                BottomNavigationBar(bottomNavItems = bottomNavItems, navController = navController)
            }
        }
    ) {
        NavigationHost(navController = navController, startDestination)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChattingApp()
}



