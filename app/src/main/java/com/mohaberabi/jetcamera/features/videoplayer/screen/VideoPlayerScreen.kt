package com.mohaberabi.jetcamera.features.videoplayer.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohaberabi.jetcamera.core.compose.EventCollector
import com.mohaberabi.jetcamera.core.compose.VideoPlayer
import com.mohaberabi.jetcamera.features.videoplayer.viewmodel.VideoPlayerActions
import com.mohaberabi.jetcamera.features.videoplayer.viewmodel.VideoPlayerEvents
import com.mohaberabi.jetcamera.features.videoplayer.viewmodel.VideoPlayerState
import com.mohaberabi.jetcamera.features.videoplayer.viewmodel.VideoViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun VideoPlayerScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = koinViewModel(),
    onShowSnackBar: (String) -> Unit,
    onSavedVideo: () -> Unit,
) {


    EventCollector(flow = viewModel.event) { event ->
        when (event) {
            VideoPlayerEvents.ErrorSavingVideo -> onShowSnackBar("error saving video...try again")
            VideoPlayerEvents.VideoSaved -> onSavedVideo()
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    VideoPlayerScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            if (action is VideoPlayerActions.OnCancelSaveVideo) {
                onSavedVideo()
            } else {
                viewModel.onAction(action)
            }
        }
    )

}


@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    state: VideoPlayerState,
    onAction: (VideoPlayerActions) -> Unit,
) {


    Column {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {


            IconButton(
                onClick = { onAction(VideoPlayerActions.OnCancelSaveVideo) },
            ) {
                Icon(
                    imageVector = Icons.Sharp.Clear,
                    contentDescription = "Close"
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {


        VideoPlayer(
            uri = state.videoPath,
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = { onAction(VideoPlayerActions.OnSaveVideo) },
            ) {
                Icon(
                    imageVector = Icons.Sharp.Send,
                    contentDescription = "Save"
                )
            }
        }
    }

}