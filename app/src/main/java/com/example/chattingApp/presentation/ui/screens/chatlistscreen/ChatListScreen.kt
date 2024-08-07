package com.example.chattingApp.presentation.ui.screens.chatlistscreen

import android.Manifest
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.tempConversations
import com.example.chattingApp.presentation.ui.screens.Screen
import com.example.chattingApp.presentation.ui.screens.profilescreen.ProfilePicture
import com.example.chattingApp.presentation.ui.util.CenterAlignedCommonAppBar
import com.example.chattingApp.presentation.ui.util.requestPermission
import com.example.chattingApp.presentation.viewmodels.ChatListViewModel
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun ChatListScreenRoot(navController: NavController) {
    val viewModel: ChatListViewModel = hiltViewModel<ChatListViewModel>()

    ChatListScreen(state = viewModel.state) { event ->
        when (event) {
            is ChatListScreenEvent.OpenConversation -> {
                if (event.conversationId.isEmpty()) {
                    // toast or error
                } else
                    navController.navigate(Screen.Chat.createRoute(event.conversationId)) {
                        /*
                         we can't know if user has chat screen with which screen ( for now)
                         but always base will be chatList, although on chatScreen we are clearing
                         instance properly but for safety for future
                         */
                        popUpTo(Screen.ChatList.route) {
                            inclusive = false
                        }
                    }
            }

            else -> {
                viewModel.onEvent(event)
            }
        }

    }
}

@Composable
fun ChatListScreen(state: ChatListScreenState, onEvent: (ChatListScreenEvent) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val permission = requestPermission {}
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onEvent(ChatListScreenEvent.ObserveConversations)
                    // comment this for preview
                    permission.launch(Manifest.permission.POST_NOTIFICATIONS)
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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedCommonAppBar(title = "Messages", leftIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    "Search",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = { })
                )
            }, actions = { ChatScreenMenuActions() })
        },
    ) { innerPadding ->
        UserListContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state,
            onEvent
        )
    }
}

@Composable
fun UserListContent(
    modifier: Modifier,
    state: ChatListScreenState,
    onEvent: (ChatListScreenEvent) -> Unit
) {
    Surface(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top),
        ) {
            items(state.conversations) {
                ConversationCard(conversation = it) { conversationId ->
                    onEvent(ChatListScreenEvent.OpenConversation(conversationId))
                }
            }
        }
    }
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
fun ConversationCard(conversation: Conversation, onClick: (String) -> Unit) {
    Surface(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick(conversation.conversationId) },
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(start = 2.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(
                conversation.iconUri, modifier = Modifier
                    .padding(4.dp)
                    .size(56.dp)
                    .padding(4.dp),
                elevation = CardDefaults.elevatedCardElevation(0.dp),
                shapes = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            ConversationDetails(
                userName = conversation.title,
                lastText = conversation.lastMessage,
                updateAt = conversation.updateAt?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ConversationDetails(
    userName: String,
    lastText: String,
    updateAt: LocalDate?,
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
        Text(text = updateAt?.toString() ?: "",
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
    ChatListScreen(ChatListScreenState(tempConversations)) {

    }
}