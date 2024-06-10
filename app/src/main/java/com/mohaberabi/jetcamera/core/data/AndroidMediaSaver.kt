package com.mohaberabi.jetcamera.core.data

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.annotation.RequiresApi
import com.mohaberabi.jetcamera.core.DispatchersProvider
import com.mohaberabi.jetcamera.core.domain.MediaSaver
import com.mohaberabi.jetcamera.core.util.AppResult
import com.mohaberabi.jetcamera.core.util.DataError
import com.mohaberabi.jetcamera.core.util.EmptyDataResult
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOError


class AndroidMediaSaver(
    private val dispatchers: DispatchersProvider,
    private val context: Context

) : MediaSaver {

    companion object {
        const val PREFIX = "jetcamera"
        const val IMG_EXT = ".jpg"
        const val IMG_MIME = "image/jpg"
        const val VIDEO_MIM = "video/mp4"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun saveImage(bitmap: Bitmap): EmptyDataResult<DataError> {
        return withContext(dispatchers.io) {
            try {
                val resolver = context.contentResolver
                val imagesCollection = MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )

                val timeMillis = System.currentTimeMillis()

                val imageValues: ContentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${timeMillis}_image" + IMG_EXT)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DCIM + "/$PREFIX"
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, IMG_MIME)
                    put(MediaStore.MediaColumns.DATE_TAKEN, timeMillis)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)

                }

                val imageMediaStoreUri: Uri? = resolver.insert(imagesCollection, imageValues)

                imageMediaStoreUri?.let { uri ->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    imageValues.clear()
                    imageValues.put(
                        MediaStore.MediaColumns.IS_PENDING, 0
                    )
                    resolver.update(
                        uri, imageValues,
                        null,
                        null
                    )
                }

                AppResult.Done(Unit)

            } catch (e: IOError) {
                e.printStackTrace()
                AppResult.Error(DataError.IO_ERROR)
            } catch (e: Exception) {
                e.printStackTrace()
                AppResult.Error(DataError.UNKNOWN)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun saveVideo(video: File): EmptyDataResult<DataError> {

        return try {
            withContext(dispatchers.io) {
                val resolver: ContentResolver = context.contentResolver

                val videoCollection = MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )

                val timeMillis = System.currentTimeMillis()

                val videoContentValues: ContentValues = ContentValues().apply {
                    put(
                        MediaStore.Video.Media.DISPLAY_NAME,
                        video.name
                    )
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DCIM + "/$PREFIX"
                    )
                    put(MediaStore.Video.Media.MIME_TYPE, VIDEO_MIM)
                    put(MediaStore.MediaColumns.DATE_ADDED, timeMillis / 1000)
                    put(MediaStore.MediaColumns.DATE_MODIFIED, timeMillis / 1000)
                    put(MediaStore.MediaColumns.DATE_TAKEN, timeMillis)
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }

                val videoMediaStoreUri: Uri? = resolver.insert(
                    videoCollection, videoContentValues
                )
                videoMediaStoreUri?.let { uri ->
                    resolver.openOutputStream(uri)?.use { output ->
                        resolver.openInputStream(Uri.fromFile(video))?.use { input ->
                            input.copyTo(output)
                        }
                    }
                    videoContentValues.clear()
                    videoContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(
                        uri, videoContentValues, null, null
                    )
                }

            }

            AppResult.Done(Unit)

        } catch (e: IOError) {
            e.printStackTrace()
            AppResult.Error(DataError.IO_ERROR)
        } catch (e: Exception) {
            e.printStackTrace()
            AppResult.Error(DataError.UNKNOWN)
        }
    }
}