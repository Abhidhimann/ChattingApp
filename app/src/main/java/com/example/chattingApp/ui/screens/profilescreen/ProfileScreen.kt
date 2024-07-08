package com.example.chattingApp.ui.screens.profilescreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.chattingApp.R
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile

@Composable
fun ProfileScreen(navController: NavController) {

    ProfileScreenContent(userId = 1, state = ProfileScreenState()) { event ->
        when (event) {
            is ProfileScreenEvent.EditProfile -> {
                navController.navigate("editProfileScreen")
            }
        }
    }
}

@Composable
fun ProfileScreenContent(
    userId: Long,
    state: ProfileScreenState,
    onEvent: (ProfileScreenEvent) -> Unit
) {
    val userProfile = state.userProfile
    Scaffold(
        topBar = {
            ProfileScreenAppBar(title = "Profile", onEvent = onEvent, isSelfUser = false)
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Profile(
            userProfile = userProfile,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenAppBar(
    title: String,
    isSelfUser: Boolean,
    onEvent: (ProfileScreenEvent) -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon =
        // todo back only when user see others profile
        {
            if (!isSelfUser) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Back",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = { })
                )
            }
        },
        actions = {
            UserProfileMenuActions(onEvent)
        }
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
        }
    }
}

@Composable
fun Profile(userProfile: UserProfile?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(
            picUrl = userProfile?.profileImageUrl,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            shapes = RoundedCornerShape(10.dp)
        )
        ProfileDetails(
            userProfile = userProfile,
            isSelfUser = true,
            Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp)
        )
    }
}

@Composable
fun ProfilePicture(picUrl: String?, modifier: Modifier, elevation: CardElevation, shapes: Shape) {
    Card(
        shape = shapes,
        elevation = elevation,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        // will use glide
        Image(
            painter = if (picUrl.isNullOrEmpty()) painterResource(id = R.drawable.dog_pic) else rememberAsyncImagePainter(
                model = R.drawable.dog_pic
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
    isSelfUser: Boolean,
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
                    modifier = Modifier.weight(0.8f)
                )
                Text(
                    text = userProfile?.gender?.value ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(.2f)
                        .padding(start = 6.dp, end = 6.dp)
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(4.dp)
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
        Spacer(modifier = Modifier.height(6.dp))
        Card(
            colors = CardDefaults.cardColors(contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
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

    ProfileScreenContent(userId = 1, state = ProfileScreenState(userProfile = tempUserProfile)) {

    }
}