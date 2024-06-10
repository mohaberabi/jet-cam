package com.mohaberabi.jetcamera.features.imageviewer.viewmodel


sealed interface ImageViewerActions {
    data object SaveImage : ImageViewerActions

    data object CancelSavingImage : ImageViewerActions
}