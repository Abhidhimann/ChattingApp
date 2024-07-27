package com.example.chattingApp.ui.screens.signin

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chattingApp.R
import com.example.chattingApp.ui.BottomNavItem
import com.example.chattingApp.ui.screens.Screen
import com.example.chattingApp.utils.SimpleLoadingScreen
import com.example.chattingApp.utils.SpannableString
import com.example.chattingApp.utils.ToastUtil
import com.example.chattingApp.utils.Validation.validateEmail
import com.example.chattingApp.viewModel.SignInViewModel

@Composable
fun SignInScreenRoot(navController: NavController) {
    val viewModel: SignInViewModel = hiltViewModel<SignInViewModel>()
    val context = LocalContext.current
    BackHandler {
        (context as? Activity)?.finishAffinity()
    }
    SignInScreen(viewModel.state) { event ->
        when (event) {
            is SignInScreenEvent.RegisterUser -> {
                navController.navigate(Screen.SignUp.route)
            }

            is SignInScreenEvent.ForgotPassword -> {
                navController.navigate(Screen.ForgotPassword.route)
            }

            is SignInScreenEvent.AfterSignIn -> {
                navController.navigate(BottomNavItem.PROFILE.route) {
                    popUpTo(Screen.SignIn.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            else -> viewModel.onEvent(event)
        }
    }
}

@Composable
fun SignInScreen(state: SignInScreenState, onEvent: (SignInScreenEvent) -> Unit) {
    Scaffold(
        topBar = {},
        modifier = Modifier.fillMaxSize(),
    ) {
        SimpleLoadingScreen(modifier = Modifier.fillMaxSize(), isLoading = state.isLoading) {
            SignInScreenContent(
                state = state,
                modifier = Modifier.padding(it),
                onEvent
            )
        }
    }
}

@Composable
fun SignInScreenContent(
    state: SignInScreenState,
    modifier: Modifier,
    onEvent: (SignInScreenEvent) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(value = false) }

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
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Password") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "email") },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                if (showPassword) {
                    IconButton(onClick = { showPassword = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "hide_password"
                        )
                    }
                } else {
                    IconButton(
                        onClick = { showPassword = true }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "hide_password"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
        )
        if (state.isSingInSuccess == false) {
            Text(
                text = state.errorMessage,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 8.dp, top = 2.dp)
            )
        } else if (state.isSingInSuccess == true) {
            ToastUtil.shortToast(LocalContext.current.applicationContext, "Login Successful")
            onEvent(SignInScreenEvent.AfterSignIn)
        }

        TextButton(
            onClick = { onEvent(SignInScreenEvent.ForgotPassword) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Forgot Password?")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                onEvent(SignInScreenEvent.SignInByEmailAndPassword(email, password))
            },
            enabled = email.isNotBlank() && password.isNotBlank() && !emailError,
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
        FooterComponent(
            modifier = Modifier.padding(bottom = 10.dp),
            onEvent
        )
    }
}

@Composable
fun FooterComponent(modifier: Modifier, onEvent: (SignInScreenEvent) -> Unit) {
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
                onClick = { onEvent(SignInScreenEvent.GoogleSso) },
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
        SpannableString(
            "Don't have an account? ",
            "Register"
        ) {
            onEvent(SignInScreenEvent.RegisterUser)
        }
    }
}


@Preview
@Composable
fun SignInScreenPreview() {
    SignInScreen(SignInScreenState()) {

    }
}