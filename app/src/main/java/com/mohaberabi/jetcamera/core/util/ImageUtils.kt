package com.mohaberabi.jetcamera.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)

}


fun Bitmap.toByteArray(): ByteArray {
    val bytesOutPutStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, bytesOutPutStream)
    val asBytes = bytesOutPutStream.toByteArray()
    return asBytes
}