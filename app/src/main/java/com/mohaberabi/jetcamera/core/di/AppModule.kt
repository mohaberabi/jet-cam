package com.mohaberabi.jetcamera.core.di

import android.widget.VideoView
import androidx.lifecycle.SavedStateHandle
import com.mohaberabi.jetcamera.core.DefaultDispatchersProvider
import com.mohaberabi.jetcamera.core.DispatchersProvider
import com.mohaberabi.jetcamera.core.data.AndroidMediaSaver
import com.mohaberabi.jetcamera.core.domain.MediaSaver
import com.mohaberabi.jetcamera.features.imageviewer.viewmodel.ImageViewerViewModel
import com.mohaberabi.jetcamera.features.videoplayer.viewmodel.VideoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {

    single<DispatchersProvider> { DefaultDispatchersProvider() }
    single<MediaSaver> { AndroidMediaSaver(get(), get()) }
    viewModelOf(::ImageViewerViewModel)
    viewModelOf(::VideoViewModel)
}