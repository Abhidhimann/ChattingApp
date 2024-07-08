package com.example.chattingApp.ui.screens.editprofilescreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import coil.compose.rememberAsyncImagePainter
import com.example.chattingApp.R
import com.example.chattingApp.domain.model.UserGender
import com.example.chattingApp.domain.model.UserProfile
import com.example.chattingApp.domain.model.tempUserProfile

@Composable
fun EditProfileScreen(userProfile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (userProfile.profileImageUrl.isEmpty()) painterResource(id = R.drawable.dog_pic) else rememberAsyncImagePainter(
                model = R.drawable.dog_pic
            ),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { },
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userProfile.userId,
            singleLine = true,
            onValueChange = { },
            label = { Text("User Id") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = userProfile.name,
            onValueChange = { },
            singleLine = true,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )


        OutlinedTextField(
            value = userProfile.aboutMe,
            onValueChange = { },
            label = { Text("About Me") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = userProfile.interests,
            maxLines = 3,
            onValueChange = { },
            label = { Text("Interests") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )


        var selectedGender by remember { mutableStateOf(userProfile.gender) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedGender == UserGender.MALE,
                onClick = { selectedGender = UserGender.MALE })
            Text(
                text = UserGender.MALE.value,
                modifier = Modifier.clickable(onClick = { selectedGender = UserGender.MALE })
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = selectedGender == UserGender.FEMALE,
                onClick = { selectedGender = UserGender.FEMALE })
            Text(
                text = UserGender.FEMALE.value,
                modifier = Modifier.clickable(onClick = { selectedGender = UserGender.FEMALE })
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = selectedGender == UserGender.OTHERS,
                onClick = { selectedGender = UserGender.OTHERS })
            Text(
                text = UserGender.OTHERS.value,
                modifier = Modifier.clickable(onClick = { selectedGender = UserGender.OTHERS })
            )
        }

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    EditProfileScreen(userProfile = tempUserProfile)
}
