package com.example.chattingApp.ui.screens.discoverscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.UserRelation
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.ui.screens.Screen
import com.example.chattingApp.ui.screens.profilescreen.ProfilePicture

import com.example.chattingApp.ui.screens.profilescreen.SimpleScreenAppBar
import com.example.chattingApp.ui.util.ToastUtil
import com.example.chattingApp.viewmodels.DiscoverViewModel

@Composable
fun DiscoverPeopleScreenRoot(navController: NavController) {
    val viewModel: DiscoverViewModel = hiltViewModel<DiscoverViewModel>()
    DiscoverPeopleScreen(state = viewModel.state) { event ->
        when (event) {
            is DiscoverScreenEvent.OtherUserProfileClicked -> navController.navigate(
                Screen.Profile.createRoute(
                    event.userId
                )
            ){
                popUpTo(Screen.SignIn.route){
                    inclusive = true
                }
            }

            else -> {
                viewModel.onEvent(event)
            }
        }
    }
}

@Composable
fun DiscoverPeopleScreen(
    state: DiscoverScreenState,
    onEvent: (DiscoverScreenEvent) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onEvent(DiscoverScreenEvent.ObserveUsers)
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
                    title = "Discover New People",
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        DiscoverPeopleScreenContent(
            state,
            onEvent,
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize()
        )
    }
}

@Composable
fun DiscoverPeopleScreenContent(
    state: DiscoverScreenState,
    onEvent: (DiscoverScreenEvent) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current.applicationContext
    LaunchedEffect(state.isRequestSuccess) {
        if (state.isRequestSuccess == true) {
            ToastUtil.shortToast(context, "Request Sent!")
        } else if (state.isRequestSuccess == false) {
            ToastUtil.shortToast(context, "Some error occurred")
        }
    }

    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = modifier) {
        itemsIndexed(state.users) { index, userProfile ->
            UserDetailsCard(userProfile = userProfile, onEvent)
        }
    }
}

@Composable
fun UserDetailsCard(userProfile: UserProfile, onEvent: (DiscoverScreenEvent) -> Unit) {
    Surface(shadowElevation = 4.dp, modifier = Modifier.padding(4.dp)) {
        Column(
            modifier = Modifier
                .clickable {
                    onEvent(DiscoverScreenEvent.OtherUserProfileClicked(userProfile.userId))
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            ProfilePicture(
                picUrl = userProfile.profileImageUrl,
                modifier = Modifier.size(100.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                shapes = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (userProfile.name.isNullOrEmpty()) "Anonymous" else userProfile.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 0.dp)
            )
            Button(onClick = {
                if (userProfile.relation != UserRelation.ALREADY_REQUESTED) {
                    onEvent(DiscoverScreenEvent.ConnectToUser(userProfile))
                } else {
                    onEvent(DiscoverScreenEvent.RemoveConnectionRequest(userProfile))
                }
            }) {
                Text(text = if (userProfile.relation == UserRelation.ALREADY_REQUESTED) "Requested" else "Connect")
            }
        }
    }
//    }
}

//     val showChatTypeDialog = remember { mutableStateOf(true) }
//if (!state.isUserProfileExists && showChatTypeDialog.value) {
//    ChatTypeAlertDialog(
//        onChatAnonymously = {
//            onEvent(DiscoverScreenEvent.ChatAnonymously)
//            onEvent(DiscoverScreenEvent.UpdateUserReadyToChatStatus(true))
//            showChatTypeDialog.value = false
//        },
//        onMakeProfile = {
//            onEvent(DiscoverScreenEvent.MakeProfile)
//            showChatTypeDialog.value = false
//        },
//        dialogText = "Would you like to chat anonymously or create a profile?",
//    )
//}

@Preview
@Composable
fun DiscoverScreenPreview() {
    DiscoverPeopleScreen(state = DiscoverScreenState(users = List(10) { tempUserProfile })) {
    }
}