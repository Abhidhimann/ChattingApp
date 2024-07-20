package com.example.chattingApp.utils

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.chattingApp.ui.screens.signupscreen.SignUpScreenContent


@Composable
fun rememberImagePickerLauncher(onImagePicked: (Uri?) -> Unit): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }
}

@Composable
fun SpannableString(
    textQuery: String,
    textClickable: String,
    textQueryStyle: SpanStyle =
        SpanStyle(
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
    textClickableStyle: SpanStyle = SpanStyle(fontSize = 15.sp, color = Color.Blue),
    onClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = textQueryStyle
        ) {
            append(textQuery)
        }
        withStyle(style = textClickableStyle) {
            pushStringAnnotation(tag = textClickable, annotation = textClickable)
            append(textClickable)
        }
    }

    ClickableText(text = annotatedString, onClick = {
        annotatedString.getStringAnnotations(it, it)
            .firstOrNull()?.also { annotation ->
                if (annotation.item == textClickable) {
                    onClick.invoke()
                }
            }
    })
}

@Composable
fun SimpleLoadingScreen(
    isLoading: Boolean,
    loadingText: @Composable ColumnScope.() -> Unit = { Text(text = "Loading") },
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.7f))
                    .fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                loadingText()
            }
        }
    }
}

