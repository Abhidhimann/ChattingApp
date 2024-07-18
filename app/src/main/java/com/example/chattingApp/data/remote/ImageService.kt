package com.example.chattingApp.data.remote

import android.net.Uri

interface ImageService {

    suspend fun uploadImageToFirebaseStorage(path: String, imageUri: Uri): String?
}