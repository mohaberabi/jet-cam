package com.mohaberabi.jetcamera.features.imageviewer.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mohaberabi.jetcamera.core.ImageViewerRoute
import com.mohaberabi.jetcamera.core.domain.MediaSaver
import com.mohaberabi.jetcamera.core.util.AppResult
import com.mohaberabi.jetcamera.core.util.toBitmap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImageViewerViewModel(
    private val mediaSaver: MediaSaver,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val image = ImageViewerRoute.fromSavedStateHandle(savedStateHandle)

    private val _state = MutableStateFlow(
        ImageViewerState(
            image = image.args.bytes.toBitmap(),
        )
    )


    private val _event = Channel<ImageViewerEvents>()
    val event = _event.receiveAsFlow()
    val state = _state.asStateFlow()

    fun onAction(action: ImageViewerActions) {
        when (action) {
            ImageViewerActions.CancelSavingImage -> Unit
            ImageViewerActions.SaveImage -> saveImage()
        }


    }


    private fun saveImage() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            val res = mediaSaver.saveImage(_state.value.image)
            when (res) {
                is AppResult.Done -> _event.send(ImageViewerEvents.ImageSaved)
                is AppResult.Error -> _event.send(ImageViewerEvents.ErrorSavingImage)
            }
        }
    }


}