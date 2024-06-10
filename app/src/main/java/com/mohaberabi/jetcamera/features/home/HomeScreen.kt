package com.mohaberabi.jetcamera.features.home

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mohaberabi.jetcamera.core.compose.CameraViewer
import java.io.File


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onImageCaptured: (Bitmap) -> Unit,
    onShowSnackBar: (String) -> Unit,
    onVideoRecorded: (File) -> Unit
) {

    CameraViewer(
        modifier = modifier,
        onImageCaptured = onImageCaptured,
        omImageCapturedError = {
            onShowSnackBar(it.message ?: "Error taking an image")
        },
        onVideoRecorded = onVideoRecorded,
        onErrorRecordingVideo = {
            onShowSnackBar("Error recoding video")
        }
    )

}