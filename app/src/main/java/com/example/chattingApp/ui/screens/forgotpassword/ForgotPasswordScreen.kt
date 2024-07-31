package com.example.chattingApp.ui.screens.forgotpassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar
import com.example.chattingApp.ui.util.ToastUtil
import com.example.chattingApp.utils.Validation.validateEmail
import com.example.chattingApp.viewmodels.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreenRoot(navController: NavController) {
    val viewModel: ForgotPasswordViewModel = hiltViewModel<ForgotPasswordViewModel>()
    ForgotPasswordScreen(ForgotPasswordState()) { event ->
        when (event) {
            is ForgotPasswordEvent.OnBackPressed -> {
                navController.popBackStack()
            }

            else -> viewModel.onEvent(event)
        }
    }
}

@Composable
fun ForgotPasswordScreen(state: ForgotPasswordState, onEvent: (ForgotPasswordEvent) -> Unit) {
    Scaffold(
        topBar = {
            SimpleScreenAppBar(
                title = "",
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        "Back",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable(onClick = { onEvent(ForgotPasswordEvent.OnBackPressed) })
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        ForgotPasswordContent(modifier = Modifier.padding(it), state, onEvent)
    }
}

@Composable
fun ForgotPasswordContent(
    modifier: Modifier,
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    val context = LocalContext.current.applicationContext
    LaunchedEffect(state.isSendingPasswordResetSuccess) {
        if (state.isSendingPasswordResetSuccess == true) {
            ToastUtil.shortToast(context, "Email send successfully.")
        } else if (state.isSendingPasswordResetSuccess == false) {
            ToastUtil.shortToast(context, "Some error occurred. Try again later.")
        }
    }
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
            text = "Forgot Password?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                letterSpacing = 1.sp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Text(
            text = "Don't worry, Please enter the email address associated with your account. We will send you a password reset email.",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 8.dp, top = 20.dp, bottom = 15.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = validateEmail(it)
            },
            singleLine = true,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)  // can add supporting text also
        )
        if (emailError) {
            Text(
                text = "Email id is incorrect.",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onEvent(ForgotPasswordEvent.SubmitEmail(email))
            },
            enabled = email.isNotBlank() && !emailError,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                "Submit!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun SingInScreenPreview() {
    ForgotPasswordScreen(ForgotPasswordState()) {

    }
}