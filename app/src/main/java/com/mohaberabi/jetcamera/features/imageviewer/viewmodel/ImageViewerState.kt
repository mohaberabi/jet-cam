package com.mohaberabi.jetcamera.features.imageviewer.viewmodel

import android.graphics.Bitmap


data class ImageViewerState(
    val image: Bitmap,
    val loading: Boolean = false
)