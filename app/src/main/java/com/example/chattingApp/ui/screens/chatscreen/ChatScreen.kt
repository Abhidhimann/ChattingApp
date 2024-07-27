package com.example.chattingApp.ui.screens.chatscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.model.MessageType
import com.example.chattingApp.domain.model.tempMessageList
import com.example.chattingApp.utils.CenterAlignedCommonAppBar
import com.example.chattingApp.utils.tempTag
import com.example.chattingApp.viewModel.ChatViewModel


@Composable
fun ChatScreenRoot(chatId: String, navController: NavController) {
    val viewModel: ChatViewModel = hiltViewModel<ChatViewModel>()

    // if action is in ui then handle by lamba function in ui ( live navigation)
    ChatScreen(chatId = chatId, state = viewModel.state) { event ->
        when (event) {
            is ChatScreenEvent.OnBackButtonPressed -> {
                navController.popBackStack()
            }

            else -> viewModel.onEvent(event)
        }
    }
}

@Composable
fun ChatScreen(chatId: String, state: ChatScreenState, onEvent: (ChatScreenEvent) -> Unit) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                onEvent(ChatScreenEvent.ObserverMessages(chatId)) // here get conversation id by state
                onEvent(ChatScreenEvent.GetConversationDetails(chatId))
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
            // todo later replace title by name
            CenterAlignedCommonAppBar(title = "Messages", leftIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Back",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable(onClick = { onEvent(ChatScreenEvent.OnBackButtonPressed) })
                )
            }, actions = { })
        },
    ) { innerPadding ->
        ChatContent(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize(), "",
            state,
            onEvent
        )
    }
}

@Composable
fun ChatContent(
    modifier: Modifier,
    userId: String,
    state: ChatScreenState,
    onEvent: (ChatScreenEvent) -> Unit = {}
) {
    val messages = state.messages

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
            items(messages) { message ->
                Log.i(tempTag(), "in class get message $message")
                ChatMessageItem(message = message)
            }
        }
        MessageInputBox(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(messageInputBox) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) { text ->
            onEvent(ChatScreenEvent.SendMessage(text))
        }
    }
}


@Composable
fun ChatMessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .align(if (message.type == MessageType.OUTGOING) Alignment.End else Alignment.Start)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (message.type == MessageType.OUTGOING) 48f else 0f,
                        bottomEnd = if (message.type == MessageType.OUTGOING) 0f else 48f
                    )
                )
                .background(MaterialTheme.colorScheme.secondary)
                .padding(10.dp)
        ) {
            Text(text = message.textContent)
        }
    }
}

@Composable
fun MessageInputBox(
    modifier: Modifier,
    onSendClickListener: (String) -> Unit,
) {
    var inputTextValue by remember { mutableStateOf(TextFieldValue("")) }
    Row(
        modifier = modifier.padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp, end = 4.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = inputTextValue,
                onValueChange = { newText ->
                    inputTextValue = newText
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    val msg = inputTextValue.text
                    if (msg.isNotBlank()) {
                        onSendClickListener(msg)
                        inputTextValue = TextFieldValue("")
                    }
                }),
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(2.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                decorationBox = { innerTextField ->
                    if (inputTextValue.text.isEmpty()) {
                        Text(
                            text = "Message",
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
        IconButton(
            onClick = {
                val msg = inputTextValue.text
                if (msg.isBlank()) return@IconButton
                onSendClickListener(msg)
                inputTextValue = TextFieldValue("")
            },
            modifier = Modifier
                .clip(CircleShape)
                .size(42.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(chatId = "", state = ChatScreenState(tempMessageList)) {
    }
}