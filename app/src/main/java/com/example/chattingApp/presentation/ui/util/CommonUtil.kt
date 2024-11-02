package com.example.chattingApp.presentation.ui.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


@Composable
fun rememberImagePickerLauncher(onImagePicked: (Uri?) -> Unit): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }
}

@Composable
fun requestPermission(callback: (Boolean) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        callback(it)
    }


@Composable
fun SpannableString(
    textQuery: String,
    textClickable: String,
    textQueryStyle: SpanStyle =
        SpanStyle(
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
    textClickableStyle: SpanStyle = SpanStyle(fontSize = 15.sp, color = Color.Blue),
    onClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = textQueryStyle
        ) {
            append(textQuery)
        }
        withStyle(style = textClickableStyle) {
            pushStringAnnotation(tag = textClickable, annotation = textClickable)
            append(textClickable)
        }
    }

    ClickableText(text = annotatedString, onClick = {
        annotatedString.getStringAnnotations(it, it)
            .firstOrNull()?.also { annotation ->
                if (annotation.item == textClickable) {
                    onClick.invoke()
                }
            }
    })
}

@Composable
fun SimpleLoadingScreen(
    isLoading: Boolean,
    modifier: Modifier,
    loadingText: @Composable ColumnScope.() -> Unit = { Text(text = "Loading") },
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        content()
        if (isLoading) {
            Box(
                modifier = modifier
                    .background(Color.Gray.copy(alpha = 0.7f))
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                loadingText()
            }
        }
    }
}

/*
 * size in mb
 */
suspend fun compressImageTillSize(context: Context, sourceFile: File, tillSize: Double): File {
    val size = tillSize * 1024 * 1024
    var compressedFile: File
    var quality = 80
    do {
        compressedFile = Compressor.compress(context, sourceFile) {
            default(quality = quality)
        }
        quality -= 10
    } while (compressedFile.length() > size)
    return compressedFile
}

fun fileFromContentUri(context: Context, contentUri: Uri): File {

    val fileExtension = getFileExtension(context, contentUri)
    val fileName = "temporary_file" + if (fileExtension != null) ".$fileExtension" else ""

    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()

    try {
        val oStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)

        inputStream?.let {
            copy(inputStream, oStream)
        }

        oStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return tempFile
}

private fun getFileExtension(context: Context, uri: Uri): String? {
    val fileType: String? = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

fun getTempFile(context: Context, suffix: String, fileName: String): File {
    val tempFile =
        File.createTempFile(fileName, suffix, context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
    return tempFile
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedCommonAppBar(
    title: String,
    leftIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    Surface(shadowElevation = 2.dp) {
        CenterAlignedTopAppBar(
            title = { Text(text = title) },
            navigationIcon = leftIcon,
            actions = actions
        )
    }
}



