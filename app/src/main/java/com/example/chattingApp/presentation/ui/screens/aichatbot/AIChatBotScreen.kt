package com.example.chattingApp.presentation.ui.screens.aichatbot

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
import com.example.chattingApp.presentation.ui.util.ToastUtil
import com.example.chattingApp.presentation.viewmodels.AIViewModel
import kotlinx.coroutines.delay


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
    val context = LocalContext.current
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        SimpleAlertDialog(
            dialogText = "Limit exceeded please start a new conversation.",
            confirmText = "New Chat",
            onConfirm = { onEvent(AIChatBotScreenEvent.OnBackButtonPressed) },
        )
    }

    LaunchedEffect(state.isSomeError) {
        if (state.isSomeError) {
            ToastUtil.shortToast(context.applicationContext, "Some error occurred.")
        }
    }
    LaunchedEffect(state.isLimitExceed) {
        if (state.isLimitExceed) {
            openDialog = true
        }
    }
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

    var loadingDotsContent by remember { mutableStateOf(".") }

    LaunchedEffect(state.isLoading) {
        while (state.isLoading) {
            for (i in 1..3) {
                loadingDotsContent = ".".repeat(i) // Update content with 1 to 3 dots
                delay(200) // Delay for smooth transition
            }
        }
    }

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
            if (state.isLoading) {
                item {
                    AIChatMessageItem(
                        AIChatMessage(
                            role = "",
                            content = loadingDotsContent,
                            type = AIChatMessageType.INCOMING
                        )
                    )
                }
            }

            items(aiChatMessages) { aiChatMessage ->
//                Log.i(tempTag(), "in class get message $aiChatMessage")
                AIChatMessageItem(aiChatMessage)
            }
        }
        MessageInputBox(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(messageInputBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }, hintText = "Message AI Bot"
        ) { text ->
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

@Composable
fun SimpleAlertDialog(
    dialogText: String,
    confirmText: String = "",
    dismissText: String = "",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        text = {
            Text(text = dialogText, style = MaterialTheme.typography.titleMedium)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (confirmText.isNotBlank()) {
                TextButton(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(confirmText)
                }
            }
        },
        dismissButton = {
            if (dismissText.isNotBlank()) {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(dismissText)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview2() {
    AIChatScreen(state = AIChatBotScreenState(aiChatMessages = tempAIChatMessageList)) {
    }
}