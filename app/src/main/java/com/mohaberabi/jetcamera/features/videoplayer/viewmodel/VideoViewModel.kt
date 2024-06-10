package com.mohaberabi.jetcamera.features.videoplayer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mohaberabi.jetcamera.core.VideoPlayerRoute
import com.mohaberabi.jetcamera.core.domain.MediaSaver
import com.mohaberabi.jetcamera.core.util.AppResult
import com.mohaberabi.jetcamera.features.imageviewer.viewmodel.ImageViewerEvents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File


class VideoViewModel(
    savedStateHandle: SavedStateHandle,
    private val mediaSaver: MediaSaver,
) : ViewModel() {


    private val videoPath = savedStateHandle.toRoute<VideoPlayerRoute>().videoPath
    private val _state = MutableStateFlow(VideoPlayerState(videoPath = videoPath))
    val state = _state.asStateFlow()


    private val _event = Channel<VideoPlayerEvents>()
    val event = _event.receiveAsFlow()
    fun onAction(action: VideoPlayerActions) {
        when (action) {
            VideoPlayerActions.OnCancelSaveVideo -> Unit
            VideoPlayerActions.OnSaveVideo -> saveVideo()
        }
    }


    private fun saveVideo() {

        viewModelScope.launch {
            val res = mediaSaver.saveVideo(File(videoPath))
            when (res) {
                is AppResult.Done -> _event.send(VideoPlayerEvents.VideoSaved)
                is AppResult.Error -> _event.send(VideoPlayerEvents.ErrorSavingVideo)
            }
        }

    }
}