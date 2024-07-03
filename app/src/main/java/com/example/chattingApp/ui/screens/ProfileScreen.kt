package com.example.chattingApp.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.chattinapp.R

@Composable
fun ProfileScreen(){
    val bottomNavItems = BottomNavItem.entries.toTypedArray()
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            MyAppBar(title = "Profile", iconImageVector = Icons.Default.ArrowBack) {}
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(tabBarItems = bottomNavItems, navController = navController) }
    ) { innerPadding ->
        Profile(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(title: String, iconImageVector: ImageVector, onIconClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon =
        // todo back only when user see others profile
        {
            Icon(
                imageVector = iconImageVector,
                "Back",
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable(onClick = { onIconClick.invoke() })
            )
        },
        actions = {
            UserProfileMenuActions()
        }
    )
}

@Composable
fun UserProfileMenuActions() {
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
                    // Handle option 1 click
                    menuExpanded = false
                }
            )
        }
    }
}

@Composable
fun Profile(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(picUrl = "", picSize = 200.dp)
        ProfileDetails(userId = "tempId", userName = "Temp User")
    }
}

@Composable
fun ProfilePicture(picUrl: String, picSize: Dp) {
    Card(
        shape = CircleShape,
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(12.dp)
    ) {
        // will use glide
        Image(
            painter = painterResource(id = R.drawable.dog_pic),
            contentDescription = "ProfilePicture",
            modifier = Modifier
                .size(picSize),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun ProfileDetails(
    userId: String,
    userName: String,
) {
    Column(
        modifier = Modifier
            .padding(bottom = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Id: $userName",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Username: $userName",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    Scaffold(
        topBar = {
            MyAppBar(title = "Profile", iconImageVector = Icons.Default.ArrowBack) {}
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Profile(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
    }
}