package com.example.chattingApp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.chattingApp.R
import com.example.chattingApp.ui.screens.signin.SignInScreenPreview

@Composable
fun StartLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // later replace it with app icon
        Image(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "null")
    }
}

@Preview
@Composable
fun StartLoadingScreenPreview() {
    StartLoadingScreen()
}
