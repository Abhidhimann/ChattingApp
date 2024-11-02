package com.example.chattingApp.presentation.ui.screens.aichatbot

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.AIChatMessageType
import com.example.chattingApp.domain.model.tempAIChatMessageList
import com.example.chattingApp.presentation.ui.screens.Screen
import com.example.chattingApp.presentation.ui.screens.chatscreen.MessageInputBox
import com.example.chattingApp.presentation.ui.util.CenterAlignedCommonAppBar
import com.example.chattingApp.presentation.viewmodels.AIViewModel
import com.example.chattingApp.utils.tempTag


@Composable
fun AIChatBotScreenRoot(navController: NavController) {
    val viewModel: AIViewModel = hiltViewModel<AIViewModel>()

    // if action is in ui then handle by lambda function in ui ( live navigation)
    AIChatScreen(state = viewModel.state) { event ->
        when (event) {
            is AIChatBotScreenEvent.OnBackButtonPressed -> {
                navController.popBackStack()
                navController.navigate(Screen.ChatList.route) {
                    popUpTo(Screen.ChatList.route) {
                        inclusive = true
                    }
                }
            }

            else -> viewModel.onEvent(event)
        }
    }

    BackHandler {
        navController.popBackStack()
        navController.navigate(Screen.ChatList.route) {
            popUpTo(Screen.ChatList.route) {
                inclusive = true
            }
        }
    }
}

@Composable
fun AIChatScreen(state: AIChatBotScreenState, onEvent: (AIChatBotScreenEvent) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedCommonAppBar(title = "AI Chat", leftIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Back",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = { onEvent(AIChatBotScreenEvent.OnBackButtonPressed) })
                )
            }, actions = { })
        },
    ) { innerPadding ->
        AIChatContent(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize(),
            state,
            onEvent
        )
    }
}

@Composable
fun AIChatContent(
    modifier: Modifier,
    state: AIChatBotScreenState,
    onEvent: (AIChatBotScreenEvent) -> Unit = {}
) {
    val aiChatMessages = state.aiChatMessages

    ConstraintLayout(modifier = modifier) {
        val (messageItems, messageInputBox) = createRefs()
        LazyColumn(reverseLayout = true, modifier = Modifier
            .fillMaxWidth()
            .constrainAs(messageItems) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(messageInputBox.top)
                height = Dimension.fillToConstraints
            }) {
            items(aiChatMessages) { aiChatMessage ->
                Log.i(tempTag(), "in class get message $aiChatMessage")
                AIChatMessageItem(aiChatMessage)
            }
        }
        MessageInputBox(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(messageInputBox) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) { text ->
            onEvent(AIChatBotScreenEvent.SendQuery(text))
        }
    }
}


@Composable
fun AIChatMessageItem(aiChatMessages: AIChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .wrapContentWidth(if (aiChatMessages.type == AIChatMessageType.OUTGOING) Alignment.End else Alignment.Start) // Wrap content based on the text size
                .align(if (aiChatMessages.type == AIChatMessageType.OUTGOING) Alignment.End else Alignment.Start)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (aiChatMessages.type == AIChatMessageType.OUTGOING) 48f else 0f,
                        bottomEnd = if (aiChatMessages.type == AIChatMessageType.OUTGOING) 0f else 48f
                    )
                )
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(10.dp)
        ) {
            Text(
                text = aiChatMessages.content,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview2() {
    AIChatScreen(state = AIChatBotScreenState(aiChatMessages = tempAIChatMessageList)) {
    }
}