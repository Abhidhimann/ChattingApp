package com.example.chattingApp.data.remote.image

import android.net.Uri
import com.example.chattingApp.utils.ResultResponse

interface ImageService {

    suspend fun uploadImageToFirebaseStorage(path: String, imageUri: Uri): ResultResponse<String>
}