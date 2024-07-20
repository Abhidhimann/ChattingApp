package com.example.chattingApp.data.remote

import android.net.Uri
import com.example.chattingApp.utils.ResultResponse

interface ImageService {

    suspend fun uploadImageToFirebaseStorage(path: String, imageUri: Uri): ResultResponse<String>
}