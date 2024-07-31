package com.example.chattingApp.ui.screens.editprofilescreen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.ui.screens.Screen
import com.example.chattingApp.ui.screens.profilescreen.ProfilePicture
import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar
import com.example.chattingApp.ui.util.SimpleLoadingScreen
import com.example.chattingApp.ui.util.ToastUtil
import com.example.chattingApp.utils.Validation.validateName
import com.example.chattingApp.ui.util.rememberImagePickerLauncher
import com.example.chattingApp.viewmodels.EditProfileViewModel


@Composable
fun EditProfileScreenRoot(navController: NavController) {
    val viewModel: EditProfileViewModel = hiltViewModel<EditProfileViewModel>()
    EditProfileScreenContent(state = viewModel.state) { event ->
        when (event) {
            is EditProfileScreenEvent.CancelOrBack -> {
                navController.navigate(Screen.Profile.route){
                    popUpTo(Screen.Profile.route){
                        inclusive = true
                    }
                    // new single instance
                }
            }
            else -> {
                viewModel.onEvent(event)
            }
        }
    }
}

@Composable
fun EditProfileScreenContent(
    state: EditProfileScreenState,
    onEvent: (EditProfileScreenEvent) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onEvent(EditProfileScreenEvent.FetchSelfProfile)
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            SimpleScreenAppBar(title = "Edit Profile", menuActions = {}, navigationIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Back",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = { onEvent(EditProfileScreenEvent.CancelOrBack) })
                )
            })
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        EditProfileContent(
            state = state,
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize(),
            onEvent
        )
    }
}

@Composable
fun EditProfileContent(
    state: EditProfileScreenState,
    modifier: Modifier,
    onEvent: (EditProfileScreenEvent) -> Unit
) {
    val maxInterestsLength = 36
    val maxAboutMeLength = 100
    val userProfile = state.userProfile
    var updateUserProfile by remember {
        mutableStateOf(userProfile)
    }
    val context = LocalContext.current.applicationContext

    LaunchedEffect(state.updatingResult) {
        if (state.updatingResult == true) {
            ToastUtil.shortToast(context, "Update Successful")
            onEvent(EditProfileScreenEvent.CancelOrBack)
        } else if (state.updatingResult == false) {
            ToastUtil.shortToast(context, "Update failed, please try again later")
        }
    }

    LaunchedEffect(state.imageUploadingResult) {
        if (state.imageUploadingResult == true) {
//            ToastUtil.shortToast(context, "Image upload Successful")
        } else if (state.imageUploadingResult == false) {
            ToastUtil.shortToast(context, "Image upload failed, please try again later")
        }
    }

    LaunchedEffect(userProfile) {
        updateUserProfile = userProfile
    }

    var nameError by remember { mutableStateOf(Pair(false, "")) }

    val imagePickerLauncher = rememberImagePickerLauncher { uri ->
        uri?.let {
            onEvent(EditProfileScreenEvent.UpdateUserPic(it))
        }
    }
    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SimpleLoadingScreen(
            modifier = Modifier
                .size(240.dp)
                .padding(4.dp)
                .clip(CircleShape),
            isLoading = state.isImageUploading
        ) {
            ProfilePicture(
                userProfile?.profileImageUrl, modifier = Modifier
                    .padding(4.dp)
                    .size(240.dp)
                    .padding(4.dp)
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                elevation = CardDefaults.elevatedCardElevation(0.dp),
                shapes = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        OutlinedTextField(
            value = updateUserProfile?.name ?: "",
            onValueChange = {
                updateUserProfile =
                    updateUserProfile?.copy(name = it)
                nameError = validateName(it)
            },
            singleLine = true,
            label = { Text("Name") },
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
            value = updateUserProfile?.aboutMe ?: "",
            onValueChange = {
                if (it.length > maxAboutMeLength) {
                    return@OutlinedTextField
                }
                val lines = it.split("\n")
                val text = if (lines.size <= 3) {
                    it
                } else {
                    lines.take(3).joinToString("\n")
                }
                updateUserProfile =
                    updateUserProfile?.copy(aboutMe = text)
            },
            label = { Text("About Me") },
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = updateUserProfile?.interests ?: "",
            maxLines = 1,
            onValueChange = {
                if (it.length < maxInterestsLength) updateUserProfile =
                    updateUserProfile?.copy(interests = it)
            },
            label = { Text("Interests") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = updateUserProfile?.gender == UserGender.MALE,
                onClick = {
                    updateUserProfile = updateUserProfile?.copy(gender = UserGender.MALE)
                })
            Text(
                text = UserGender.MALE.value,
                modifier = Modifier.clickable(onClick = {
                    updateUserProfile = updateUserProfile?.copy(gender = UserGender.MALE)
//                    selectedGender = UserGender.MALE
                })
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = updateUserProfile?.gender == UserGender.FEMALE,
                onClick = {
                    updateUserProfile = updateUserProfile?.copy(gender = UserGender.FEMALE)
                })
            Text(
                text = UserGender.FEMALE.value,
                modifier = Modifier.clickable(onClick = {
                    updateUserProfile = updateUserProfile?.copy(gender = UserGender.FEMALE)
                })
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = updateUserProfile?.gender == UserGender.OTHERS,
                onClick = {
//                    selectedGender = UserGender.OTHERS
                    updateUserProfile = updateUserProfile?.copy(gender = UserGender.OTHERS)
                }
            )
            Text(
                text = UserGender.OTHERS.value,
                modifier = Modifier.clickable(onClick = {
                    updateUserProfile = updateUserProfile?.copy(gender = UserGender.OTHERS)
                })
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
        ) {
            Button(
                onClick = { onEvent(EditProfileScreenEvent.CancelOrBack) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .widthIn(max = 200.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel")
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Button(
                onClick = {
                    if (updateUserProfile == null) {
                        return@Button
                        // todo toast
                    }
                    if (state.isImageUploading) {
                        ToastUtil.shortToast(context, "Please wait image is uploading")
                        return@Button
                    }
                    onEvent(EditProfileScreenEvent.UpdateProfileDetails(updateUserProfile!!))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .widthIn(max = 200.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    EditProfileScreenContent(state = EditProfileScreenState(tempUserProfile)) {

    }
}
