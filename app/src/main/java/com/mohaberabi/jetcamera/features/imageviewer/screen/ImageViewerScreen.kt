package com.mohaberabi.jetcamera.features.imageviewer.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohaberabi.jetcamera.R
import com.mohaberabi.jetcamera.core.compose.EventCollector
import com.mohaberabi.jetcamera.features.imageviewer.viewmodel.ImageViewerActions
import com.mohaberabi.jetcamera.features.imageviewer.viewmodel.ImageViewerEvents
import com.mohaberabi.jetcamera.features.imageviewer.viewmodel.ImageViewerState
import com.mohaberabi.jetcamera.features.imageviewer.viewmodel.ImageViewerViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun ImageViewerScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: ImageViewerViewModel = koinViewModel(),
    onShowSnackBar: (String) -> Unit,
    onImageSaved: () -> Unit,
) {


    val context = LocalContext.current
    EventCollector(
        flow = viewModel.event,
    ) { event ->
        when (event) {
            ImageViewerEvents.ErrorSavingImage -> onShowSnackBar(context.getString(R.string.error_saving_image_try_again))
            ImageViewerEvents.ImageSaved -> onImageSaved()
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()


    ImageViewerScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            if (action == ImageViewerActions.CancelSavingImage) {
                onImageSaved()
            } else {
                viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    state: ImageViewerState,
    onAction: (ImageViewerActions) -> Unit
) {


    Column(
        modifier = modifier,

        ) {


        Row(
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = { onAction(ImageViewerActions.CancelSavingImage) },
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "cancel"
                )
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            bitmap = state.image.asImageBitmap(),
            modifier = Modifier.fillMaxSize(),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )

        Row(horizontalArrangement = Arrangement.End) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onAction(ImageViewerActions.SaveImage)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(R.string.save)
                )
            }

        }

    }


}