package com.mohaberabi.jetcamera.core.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executor


@Composable
fun CameraViewer(
    modifier: Modifier = Modifier,
    onImageCaptured: (Bitmap) -> Unit,
    omImageCapturedError: (ImageCaptureException) -> Unit,
    videoOutputPath: String = "${System.currentTimeMillis()}_video",
    onVideoRecorded: (File) -> Unit = {},
    onErrorRecordingVideo: () -> Unit = {}
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    var isRecording by remember {
        mutableStateOf(false)
    }
    val currentRecoding = remember {
        mutableStateOf<Recording?>(null)
    }
    val context = LocalContext.current
    val videoOutputFile = remember {
        File(
            context.filesDir,
            "$videoOutputPath.mp4"
        )
    }
    val controller = remember {
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)

        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView(
            factory = {
                // [PreviewView] camerax view of the camera
                PreviewView(it).apply {
                    this.controller = controller
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    controller.bindToLifecycle(lifeCycleOwner)
                }
            },
            modifier = modifier,
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CaptureButton(
                onLongClick = {
                    if (isRecording) {
                        return@CaptureButton
                    }
                    isRecording = true
                    startRecodingVideo(
                        file = videoOutputFile,
                        executor = ContextCompat.getMainExecutor(context),
                        onRecorded = { onVideoRecorded(videoOutputFile) },
                        onErrorRecording = onErrorRecordingVideo,
                        recodingState = currentRecoding,
                        controller = controller
                    )

                },
                onClick = {
                    if (isRecording) {
                        isRecording = false
                        currentRecoding.value?.close()
                        currentRecoding.value = null
                    } else {

                        takePicture(
                            controller = controller,
                            context = context,
                            omImageCapturedError = omImageCapturedError,
                            onImageCaptured = onImageCaptured,
                        )
                    }

                },
            )
            if (!isRecording)
                CircleButton(
                    onClick = {
                        switchCamera(controller)
                    },
                    icon = Icons.Default.Refresh
                )

        }


    }


}

private fun takePicture(
    controller: LifecycleCameraController,
    context: Context,
    onImageCaptured: (Bitmap) -> Unit,
    omImageCapturedError: (ImageCaptureException) -> Unit,
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        ImageCaptureHandler(
            onImageCaptured = onImageCaptured,
            omImageCapturedError = omImageCapturedError
        )
    )
}

@Suppress("missingPermission")
private fun startRecodingVideo(
    executor: Executor,
    onRecorded: () -> Unit,
    onErrorRecording: () -> Unit,
    file: File,
    controller: LifecycleCameraController,
    recodingState: MutableState<Recording?>
) {
    val outputOptions = FileOutputOptions.Builder(file).build()
    recodingState.value = controller.startRecording(
        outputOptions,
        AudioConfig.create(true),
        executor,
    ) { event ->
        if (event is VideoRecordEvent.Finalize) {

            if (event.hasError()) {
                recodingState.value?.close()
                recodingState.value = null
                onErrorRecording()
            } else {
                onRecorded()
            }
        }
    }


}


@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector
) {

    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .clip(CircleShape)
            .size(32.dp)
            .background(Color.White)
    )
    {
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
    }
}

private fun switchCamera(
    controller: LifecycleCameraController,
) {
    controller.cameraSelector =
        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
}

class ImageCaptureHandler(
    val onImageCaptured: (Bitmap) -> Unit,
    val omImageCapturedError: (ImageCaptureException) -> Unit,
) : ImageCapture.OnImageCapturedCallback() {

    override fun onError(exception: ImageCaptureException) {
        super.onError(exception)
        omImageCapturedError(exception)
    }

    override fun onCaptureSuccess(image: ImageProxy) {
        super.onCaptureSuccess(image)
        val matrix = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }
        val bitmap = Bitmap.createBitmap(
            image.toBitmap(),
            0,
            0,
            image.width,
            image.height,
            matrix,
            true
        )
        onImageCaptured(bitmap)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CaptureButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .padding(bottom = 12.dp)
            .background(Color.White)
            .size(60.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )

    ) {
        icon?.let {
            Icon(imageVector = icon, contentDescription = null)
        }

    }

}


