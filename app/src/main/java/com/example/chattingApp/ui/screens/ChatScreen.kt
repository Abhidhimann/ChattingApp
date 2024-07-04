package com.example.chattingApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.model.messageList

@Composable
fun ChatScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatScreenAppBar(title = "Messages") {}
        },
    ) { innerPadding ->
        ChatSurface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(), 13
        )
    }
}

@Composable
fun ChatSurface(modifier: Modifier, userId: Long) {
    val messages = remember {
        messageList
    }
    LaunchedEffect(messages.size) {

    }
    ConstraintLayout(modifier = modifier) {
        val (messageItems, messageInputBox) = createRefs()
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(messageItems) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(messageInputBox.top)
                height = Dimension.fillToConstraints
            }) {
            items(messages) { message ->
                ChatMessageItem(message = message, isSelf = message.senderId == userId)
            }
        }
        MessageInputBox(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(messageInputBox) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) { text ->
            messages.add(Message(12, text, userId, 12, ""))
        }
    }
}


@Composable
fun ChatMessageItem(message: Message, isSelf: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .align(if (isSelf) Alignment.End else Alignment.Start)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isSelf) 48f else 0f,
                        bottomEnd = if (isSelf) 0f else 48f
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
    Row(modifier = modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
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
                onSendClickListener(inputTextValue.text)
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
    ChatScreen()
}