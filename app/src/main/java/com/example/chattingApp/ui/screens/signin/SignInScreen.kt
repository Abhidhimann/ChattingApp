package com.example.chattingApp.ui.screens.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chattingApp.R

@Composable
fun SingInScreenRoot() {

    SingInScreen(SingInScreenState()) {

    }
}

@Composable
fun SingInScreen(state: SingInScreenState, onEvent: (SingInScreenEvent) -> Unit) {
    Scaffold(
        topBar = {},
        modifier = Modifier.fillMaxSize(),
    ) {
        SingInScreenContent(modifier = Modifier.padding(it), onEvent)
    }
}

@Composable
fun SingInScreenContent(modifier: Modifier, onEvent: (SingInScreenEvent) -> Unit) {
    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                letterSpacing = 1.sp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = "",
            onValueChange = {
            },
            singleLine = true,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)  // can add supporting text also
        )


        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Password") },
            maxLines = 3,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onEvent(SingInScreenEvent.SignInByEmail)
            },
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                "Sign In!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        FooterComponent(modifier = Modifier.padding(bottom = 10.dp), onEvent)
    }
}

@Composable
fun FooterComponent(modifier: Modifier, onEvent: (SingInScreenEvent) -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                thickness = 1.dp,
            )
            Text(
                text = "OR",
                modifier = Modifier.padding(10.dp),
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                thickness = 1.dp,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { onEvent(SingInScreenEvent.GoogleSso) },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                    MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Sign in with google",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        SpannableString("Don't have an account? ", "Register") {
            onEvent(SingInScreenEvent.RegisterUser)
        }
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

@Preview
@Composable
fun SingInScreenPreview() {
    SingInScreen(SingInScreenState()) {

    }
}