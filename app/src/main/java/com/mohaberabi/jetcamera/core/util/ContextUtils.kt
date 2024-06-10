package com.mohaberabi.jetcamera.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED


fun Context.hasAllowedCameraPermission(): Boolean {
    val allowedCamera = hasPermission(Manifest.permission.CAMERA)
    val allowedAudio = hasPermission(Manifest.permission.RECORD_AUDIO)
    return allowedCamera && allowedAudio
}

