package com.example.chattingApp.data.remote

import android.net.Uri
import android.util.Log
import com.example.chattingApp.utils.ResultResponse
import com.example.chattingApp.utils.classTag
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.util.UUID

class ImageServiceImpl(private val storageRef: StorageReference) : ImageService {
    override suspend fun uploadImageToFirebaseStorage(
        path: String,
        imageUri: Uri
    ): ResultResponse<String> {
        return withContext(Dispatchers.IO) {
            val imageRef = storageRef.child("$path/${UUID.randomUUID()}.jpg")
            try {
                imageRef.putFile(imageUri).await()
                ResultResponse.Success(imageRef.downloadUrl.await().toString())
            } catch (e: Exception) {
                Log.e(classTag(), "Image $imageUri uploading failed -> $e")
                ResultResponse.Failed(e)
            }
        }
    }
}