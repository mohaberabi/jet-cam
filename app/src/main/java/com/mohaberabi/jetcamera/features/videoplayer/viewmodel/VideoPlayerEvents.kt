package com.mohaberabi.jetcamera.features.videoplayer.viewmodel

interface VideoPlayerEvents {
    data object VideoSaved : VideoPlayerEvents
    data object ErrorSavingVideo : VideoPlayerEvents
}