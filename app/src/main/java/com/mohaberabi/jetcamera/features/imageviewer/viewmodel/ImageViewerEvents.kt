package com.mohaberabi.jetcamera.features.imageviewer.viewmodel

sealed interface ImageViewerEvents {


    data object ImageSaved : ImageViewerEvents
    data object ErrorSavingImage : ImageViewerEvents
}