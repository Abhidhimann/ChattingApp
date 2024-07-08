package com.example.chattingApp.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter

object Helper {

    @Composable
    fun ImageComponent(imageUrl: String, imageLoader: ImageLoader, contentDescription: String, modifier: Modifier) {
        // Use Coil's Image component with rememberAsyncImagePainter for asynchronous image loading
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl, imageLoader = imageLoader),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}