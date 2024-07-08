package com.example.chattingApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile
import com.example.chattingApp.ui.screens.profilescreen.ProfilePicture

@Composable
fun ChatListScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatScreenAppBar(title = "Messages") {}
        },
    ) { innerPadding ->
        UserListSurface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            users = listOf(tempUserProfile),
            navController = navController
        )
    }
}

@Composable
fun UserListSurface(
    modifier: Modifier,
    users: List<UserProfile>,
    navController: NavController
) {
    Surface(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top),
        ) {
            items(users) {
                ProfileCard(userProfile = it) {
                    navController.navigate(route = "chatScreen")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenAppBar(title: String, onIconClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon =
        {
            Icon(
                imageVector = Icons.Default.Search,
                "Search",
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable(onClick = { onIconClick.invoke() })
            )
        },
        actions = { ChatScreenMenuActions() }
    )
}

@Composable
fun ChatScreenMenuActions() {
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
                text = { Text("Clear all Chats") },
                onClick = {
                    // will do something here
                    menuExpanded = false
                }
            )
        }
    }
}

@Composable
fun ProfileCard(userProfile: UserProfile, onClick: () -> Unit) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick.invoke() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(
                userProfile.profileImageUrl, modifier = Modifier
                    .padding(4.dp)
                    .size(56.dp)
                    .padding(4.dp)
                ,
                elevation = CardDefaults.elevatedCardElevation(0.dp),
                shapes = CircleShape
            )
            ChatThreadDetails(
                userName = userProfile.name,
                lastText = "Hi how are you",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ChatThreadDetails(
    userName: String,
    lastText: String,
    modifier: Modifier,
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (title, lastMessage, lastUpdateTime) = createRefs()
        Text(
            text = userName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .wrapContentHeight()
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = lastText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .wrapContentHeight()
                .constrainAs(lastMessage) {
                    top.linkTo(title.bottom, margin = 4.dp)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(text = "8:08 PM",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .wrapContentHeight()
                .constrainAs(lastUpdateTime) {
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(parent.top, margin = 4.dp)
                })
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    ChatListScreen(rememberNavController())
}