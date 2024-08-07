package com.example.chattingApp.ui.screens.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.chattingApp.R

@Composable
fun StartUpLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Spacer(modifier = Modifier.weight(1f))
//        Image(
//            painter = painterResource(id = R.mipmap.app_icon_foreground),
//            contentDescription = "null",
//        )
//        Spacer(modifier = Modifier.weight(1f))
//        Text(text = "By")
//        Text(
//            text = "Abhishek",
//            style = MaterialTheme.typography.titleLarge,
//            color = MaterialTheme.colorScheme.onPrimaryContainer,
//            fontWeight = FontWeight.Bold
//        )
//        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview
@Composable
fun StartLoadingScreenPreview() {
    StartUpLoading()
}
