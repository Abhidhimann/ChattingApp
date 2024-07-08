package com.example.chattingApp.ui.screens.editprofilescreen


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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.ui.BottomNavItem
import com.example.chattingApp.ui.screens.profilescreen.ProfilePicture
import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar


@Composable
fun EditProfileScreenRoot(navController: NavController) {

    EditProfileScreenContent(userId = 1, state = EditProfileScreenState()) { event ->
        when (event) {
            is EditProfileScreenEvent.Cancel -> navController.navigate(BottomNavItem.PROFILE.route)
            else -> Unit
        }
        // viewModel action
    }

}

@Composable
fun EditProfileScreenContent(
    userId: Long,
    state: EditProfileScreenState,
    onEvent: (EditProfileScreenEvent) -> Unit
) {
    val userProfile = state.profile
    Scaffold(
        topBar = {
            SimpleScreenAppBar(title = "Edit Profile", menuActions = {}, navigationIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Back",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = { onEvent(EditProfileScreenEvent.Cancel) })
                )
            })
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        EditProfileScreenSurface(
            userProfile2 = userProfile,
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
    userProfile2: UserProfile?,
    modifier: Modifier,
    onEvent: (EditProfileScreenEvent) -> Unit
) {
    val newUser = UserProfile(
        name = "",
        email = "",
        password = "",
        userId = "",
        profileImageUrl = "",
        gender = UserGender.MALE,
        aboutMe = "",
        age = 0,
        interests = ""
    )
    val maxCharId = 16
    val maxCharName = 16 // use enum on constant somewhere
    var userProfile by remember {
        mutableStateOf(userProfile2 ?: newUser)
    }
    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(
            userProfile.profileImageUrl, modifier = Modifier
                .padding(4.dp)
                .size(240.dp)
                .padding(4.dp),
            elevation = CardDefaults.elevatedCardElevation(0.dp),
            shapes = CircleShape
        )


        OutlinedTextField(
            value = userProfile.name,
            onValueChange = {
                if (it.length < maxCharName) userProfile = userProfile.copy(name = it)
            },
            singleLine = true,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)  // can add supporting text also
        )


        OutlinedTextField(
            value = userProfile.aboutMe,
            onValueChange = { userProfile = userProfile.copy(aboutMe = it) },
            label = { Text("About Me") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = userProfile.interests,
            maxLines = 3,
            onValueChange = { userProfile = userProfile.copy(interests = it) },
            label = { Text("Interests") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )


        var selectedGender by remember { mutableStateOf(userProfile.gender) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedGender == UserGender.MALE,
                onClick = { selectedGender = UserGender.MALE })
            Text(
                text = UserGender.MALE.value,
                modifier = Modifier.clickable(onClick = { selectedGender = UserGender.MALE })
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = selectedGender == UserGender.FEMALE,
                onClick = { selectedGender = UserGender.FEMALE })
            Text(
                text = UserGender.FEMALE.value,
                modifier = Modifier.clickable(onClick = { selectedGender = UserGender.FEMALE })
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = selectedGender == UserGender.OTHERS,
                onClick = { selectedGender = UserGender.OTHERS })
            Text(
                text = UserGender.OTHERS.value,
                modifier = Modifier.clickable(onClick = { selectedGender = UserGender.OTHERS })
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
        ) {
            Button(
                onClick = { onEvent(EditProfileScreenEvent.Cancel) },
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
                onClick = { },
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
    EditProfileScreenContent(userId = 1, state = EditProfileScreenState(tempUserProfile)) {

    }
}
