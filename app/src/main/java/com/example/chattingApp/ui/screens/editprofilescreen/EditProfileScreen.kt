package com.example.chattingApp.ui.screens.editprofilescreen


import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.ui.BottomNavItem
import com.example.chattingApp.ui.screens.profilescreen.ProfilePicture
import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar
import com.example.chattingApp.utils.ToastUtil
import com.example.chattingApp.utils.rememberImagePickerLauncher
import com.example.chattingApp.viewModel.EditProfileViewModel


@Composable
fun EditProfileScreenRoot(navController: NavController) {
    val viewModel: EditProfileViewModel = hiltViewModel<EditProfileViewModel>()
    EditProfileScreenContent(state = viewModel.state) { event ->
        when (event) {
            is EditProfileScreenEvent.CancelOrBack -> navController.navigate(BottomNavItem.goToProfileRoute())
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

    val userProfile = state.userProfile
    val context = LocalContext.current.applicationContext

    LaunchedEffect(state) {
        Log.d("ABHITAG", "user profile1 is ${state.userProfile}")
    }

    if (state.updatingResult == true) {
        LaunchedEffect(state.updatingResult) {
            ToastUtil.shortToast(context, "Update Successful")
//            onEvent(EditProfileScreenEvent.CancelOrBack)
        }
    } else if (state.updatingResult == false) {
        LaunchedEffect(state.updatingResult) {
            ToastUtil.shortToast(context, "Update failed, please try again later")
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
        EditProfileScreenSurface(
            userProfile = userProfile,
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize(),
            onEvent
        )
    }
}

@Composable
fun EditProfileScreenSurface(
    userProfile: UserProfile?,
    modifier: Modifier,
    onEvent: (EditProfileScreenEvent) -> Unit
) {
    val maxNameLength = 16 // use enum on constant somewhere
    val maxInterestsLength = 36
    var updateUserProfile by remember {
        mutableStateOf(userProfile)
    }
    LaunchedEffect(userProfile) {
        Log.i("ABHITAG", "user profile is $userProfile")
        updateUserProfile = userProfile
    }

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


        OutlinedTextField(
            value = updateUserProfile?.name ?: "",
            onValueChange = {
                if (it.length < maxNameLength) updateUserProfile =
                    updateUserProfile?.copy(name = it)
            },
            singleLine = true,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)  // can add supporting text also
        )


        OutlinedTextField(
            value = updateUserProfile?.aboutMe ?: "",
            onValueChange = {
                updateUserProfile = updateUserProfile?.copy(aboutMe = it)
            },
            label = { Text("About Me") },
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
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
                        // todo toast
                    } else {
                        onEvent(EditProfileScreenEvent.UpdateProfileDetails(updateUserProfile!!))
                    }
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
