package com.mohaberabi.jetcamera.features.videoplayer.viewmodel

sealed interface VideoPlayerActions {


    data object OnSaveVideo : VideoPlayerActions
    data object OnCancelSaveVideo : VideoPlayerActions

}