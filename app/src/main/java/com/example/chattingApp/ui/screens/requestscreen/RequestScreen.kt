package com.example.chattingApp.ui.screens.requestscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.ui.BottomNavItem
import com.example.chattingApp.ui.screens.profilescreen.ProfilePicture
import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar
import com.example.chattingApp.viewModel.RequestScreenViewModel

@Composable
fun RequestScreenRoot(navController: NavController) {
    val viewModel: RequestScreenViewModel = hiltViewModel<RequestScreenViewModel>()
    RequestScreen(state = viewModel.state) { event ->
        when (event) {
            is RequestScreenEvent.RequestedUserProfileClicked -> navController.navigate(
                BottomNavItem.goToProfileRoute(
                    event.userId
                )
            )

            else -> viewModel.onEvent(event)
        }
    }
}

@Composable
fun RequestScreen(
    state: RequestScreenState,
    onEvent: (RequestScreenEvent) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onEvent(RequestScreenEvent.ObserveRequestUsers)
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
            Surface(shadowElevation = 4.dp) {
                SimpleScreenAppBar(
                    title = "Requests",
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        RequestScreenContent(modifier = Modifier.padding(innerPadding), state, onEvent)
    }
}

@Composable
fun RequestScreenContent(
    modifier: Modifier,
    state: RequestScreenState,
    onEvent: (RequestScreenEvent) -> Unit
) {
    val requestedUsers = state.requestedUsers
    LazyColumn(modifier = modifier) {
        items(requestedUsers) { userProfile ->
            requestUserCard(userProfile = userProfile, onEvent)
        }
    }
}

@Composable
fun requestUserCard(userProfile: UserProfile, onEvent: (RequestScreenEvent) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            ProfilePicture(
                picUrl = userProfile.profileImageUrl,
                modifier = Modifier
                    .size(42.dp)
                    .clickable {
                        onEvent(RequestScreenEvent.RequestedUserProfileClicked(userProfile.userId))
                    },
                elevation = CardDefaults.elevatedCardElevation(2.dp),
                shapes = CircleShape,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = (if (userProfile.name.isNullOrEmpty()) "Anonymous" else userProfile.name) + " wants to message you.",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .weight(0.7f)
                    .clickable {
                        onEvent(RequestScreenEvent.RequestedUserProfileClicked(userProfile.userId))
                    },
            )
            Row(
                modifier = Modifier
                    .padding(start = 2.dp, end = 4.dp)
                    .wrapContentWidth()
            ) {
                OutlinedIconButton(
                    onClick = {
                        onEvent(RequestScreenEvent.RejectRequest(userProfile.userId))
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Accepts",
                        modifier = Modifier.padding(2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                val checkButtonClick = remember {
                    mutableStateOf(false)
                }
                OutlinedIconButton(
                    onClick = {
                        checkButtonClick.value = true
                        onEvent(RequestScreenEvent.AcceptRequest(userProfile.toUserSummary()))
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        "Accepts",
                        modifier = Modifier
                            .background(
                                if (checkButtonClick.value) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer
                            )
                            .padding(2.dp),
                        tint = if (checkButtonClick.value) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RequestScreenPreview() {
    RequestScreen(state = RequestScreenState(List(10) { tempUserProfile })) {

    }
}