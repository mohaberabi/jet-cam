package com.mohaberabi.jetcamera

import android.app.Application
import com.mohaberabi.jetcamera.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class JetCameraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@JetCameraApplication)
            androidLogger()
            modules(
                appModule,
            )
        }
    }
}