package com.mohaberabi.jetcamera.core.domain

import android.graphics.Bitmap
import com.mohaberabi.jetcamera.core.util.AppError
import com.mohaberabi.jetcamera.core.util.AppResult
import com.mohaberabi.jetcamera.core.util.DataError
import com.mohaberabi.jetcamera.core.util.EmptyDataResult
import java.io.File

interface MediaSaver {


    suspend fun saveImage(bitmap: Bitmap): EmptyDataResult<DataError>
    suspend fun saveVideo(video: File): EmptyDataResult<DataError>
}