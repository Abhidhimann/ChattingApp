package com.example.chattingApp.ui.screens.signupscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chattingApp.ui.screens.Screen
import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar
import com.example.chattingApp.utils.SimpleLoadingScreen
import com.example.chattingApp.utils.SpannableString
import com.example.chattingApp.utils.ToastUtil
import com.example.chattingApp.utils.Validation.validateEmail
import com.example.chattingApp.utils.Validation.validateName
import com.example.chattingApp.utils.Validation.validatePassword
import com.example.chattingApp.viewModel.SignUpViewModel

@Composable
fun SignUpScreenRoot(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()
    SignUpScreen(viewModel.state) { event ->
        when (event) {
            is SignUpScreenEvent.Login -> {
                navController.navigate(Screen.SignIn.route)
            }
            
            is SignUpScreenEvent.OnBackPressed -> {
                navController.popBackStack()
            }

            else -> viewModel.onEvent(event)
        }
    }
}

@Composable
fun SignUpScreen(state: SignUpScreenState, onEvent: (SignUpScreenEvent) -> Unit) {
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
                            .clickable(onClick = { onEvent(SignUpScreenEvent.OnBackPressed) })
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        SimpleLoadingScreen(modifier = Modifier.fillMaxSize(), isLoading = state.isLoading) {
            SignUpScreenContent(
                modifier = Modifier.padding(it), state, onEvent
            )
        }
    }
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier,
    state: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(Pair(false, "")) }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember {
        mutableStateOf(Pair(false, ""))
    }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current.applicationContext

    if (state.isSignUpSuccess == true) {
        ToastUtil.longToast(
            context,
            "Account created. We have send you a verification email."
        )
        onEvent(SignUpScreenEvent.Login)
    } else if (state.isSignUpSuccess == false) {
        ToastUtil.shortToast(
            context,
            state.errorMessage
        )
        onEvent(SignUpScreenEvent.ResetSingUpState)
    }

    // todo name limit
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
            text = "Create an Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                letterSpacing = 1.sp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = validateName(it)
            },
            singleLine = true,
            label = { Text("Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)  // can add supporting text also
        )
        if (nameError.first) {
            Text(
                text = nameError.second,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 8.dp)
            )
        }
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
                text = "Email id is not right.",
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
                passwordError = validatePassword(it)
            },
            label = { Text("Password") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "enter password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (passwordError.first) {
            Text(
                text = passwordError.second,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 8.dp)
            )
        }
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            leadingIcon = {
                if (confirmPassword != password) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "password not matching",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "password matching",
                    )
                }
            },
            isError = confirmPassword != password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onEvent(SignUpScreenEvent.SignUp(email, password, name))
            },
            enabled = email.isNotBlank() && name.isNotBlank() && password.isNotBlank()
                    && confirmPassword.isNotBlank() && !emailError && !nameError.first && !passwordError.first
                    && password == confirmPassword,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                "Sign Up!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        SpannableString("Already have an account? ", "Login") {
            onEvent(SignUpScreenEvent.Login)
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview
@Composable
fun SingInScreenPreview() {
    SignUpScreen(SignUpScreenState()) {

    }
}