package com.example.chattingApp.presentation.ui.screens.profilescreen

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.chattingApp.R
import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.presentation.ui.screens.Screen
import com.example.chattingApp.presentation.ui.util.ToastUtil
import com.example.chattingApp.presentation.viewmodels.ProfileScreenViewModel

@Composable
fun ProfileScreenRoot(userId: String, navController: NavController) {
    val viewModel: ProfileScreenViewModel = hiltViewModel<ProfileScreenViewModel>()
    ProfileScreen(userId = userId, state = viewModel.state) { event ->
        when (event) {
            is ProfileScreenEvent.EditProfile -> {
                navController.navigate(Screen.EditProfile.route) {
                    popUpTo(Screen.EditProfile.route) {
                        inclusive = true
                    }
                }
            }

            is ProfileScreenEvent.OnBackPressed -> {
                navController.popBackStack()
            }

//            is ProfileScreenEvent.LogOut -> {
//                navController.clearBackStack(navController.graph.id)
//                viewModel.onEvent(event)
//            }

            else -> {
                viewModel.onEvent(event)
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userId: String,
    state: ProfileScreenState,
    onEvent: (ProfileScreenEvent) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onEvent(ProfileScreenEvent.FetchUserProfile(userId))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    }
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.isLogoutSuccess) {
        if (state.isLogoutSuccess == false) {
            ToastUtil.shortToast(context, "Some error occurred. Please try again later.")
        } else if (state.isLogoutSuccess == true) {
            ToastUtil.shortToast(context, "Sign out successfully")
        }
    }


    val userProfile = state.userProfile
    Scaffold(
        topBar = {
            SimpleScreenAppBar(
                title = "Profile",
                menuActions = { if (userId.isEmpty()) UserProfileMenuActions(onEvent) },
                navigationIcon = {
                    if (userId.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            "Back",
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clickable(onClick = { onEvent(ProfileScreenEvent.OnBackPressed) })
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        ProfileScreenContent(
            userProfile = userProfile,
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleScreenAppBar(
    title: String,
    menuActions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = navigationIcon,
        actions = menuActions,
    )
}

@Composable
fun UserProfileMenuActions(onEvent: (ProfileScreenEvent) -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Profile") },
                onClick = {
                    onEvent(ProfileScreenEvent.EditProfile(tempUserProfile))
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Log out") },
                onClick = {
                    onEvent(ProfileScreenEvent.LogOut)
                    menuExpanded = false
                }
            )
        }
    }
}

@Composable
fun ProfileScreenContent(userProfile: UserProfile?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(start = 12.dp, end = 12.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (profilePicture, profileDetails) = createRefs()

            ProfilePicture(
                picUrl = userProfile?.profileImageUrl,
                modifier = Modifier
                    .constrainAs(profilePicture) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(profileDetails.top)
                        height = Dimension.wrapContent
                    }
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(start = 2.dp, end = 2.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                shapes = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            ProfileDetails(
                userProfile = userProfile,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .constrainAs(profileDetails) {
                        top.linkTo(profilePicture.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}


@Composable
fun ProfilePicture(
    picUrl: String?,
    modifier: Modifier,
    elevation: CardElevation,
    shapes: Shape,
    border: BorderStroke
) {
    Card(
        shape = shapes,
        elevation = elevation,
        border = border,
        modifier = modifier
    ) {
        // will use glide
        Image(
            painter = if (picUrl.isNullOrEmpty()) painterResource(id = R.drawable.user_default_pic) else rememberAsyncImagePainter(
                model = picUrl
            ),
            contentDescription = "ProfilePicture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun ProfileDetails(
    userProfile: UserProfile?,
    horizontalAlignment: Alignment.Horizontal,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = horizontalAlignment
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (!userProfile?.name.isNullOrBlank()) {
                Text(
                    text = (userProfile?.name ?: ""),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(0.6f)
                )
                Spacer(modifier = Modifier.width(32.dp))
                Icon(
                    imageVector = if (userProfile?.gender == UserGender.MALE) Icons.Filled.Male else if (userProfile?.gender == UserGender.FEMALE) Icons.Filled.Female else Icons.Filled.Transgender,
                    contentDescription = "gender",
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = userProfile?.gender?.value ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .wrapContentWidth()
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(2.dp)
        ) {
            Text(
                text = "About me",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(6.dp)
            )
            Text(
                text = userProfile?.aboutMe ?: "",
                modifier = Modifier.padding(start = 8.dp, end = 6.dp, bottom = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(
                text = "Interests",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(6.dp)
            )
            Text(
                text = userProfile?.interests ?: "",
                modifier = Modifier.padding(start = 8.dp, end = 6.dp, bottom = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {

    ProfileScreen(userId = "", state = ProfileScreenState(userProfile = tempUserProfile)) {

    }
}