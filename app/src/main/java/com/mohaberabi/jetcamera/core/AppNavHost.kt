package com.mohaberabi.jetcamera.core

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mohaberabi.jetcamera.core.util.serializableType
import com.mohaberabi.jetcamera.core.util.toByteArray
import com.mohaberabi.jetcamera.features.home.HomeScreen
import com.mohaberabi.jetcamera.features.imageviewer.screen.ImageViewerScreenRoot
import com.mohaberabi.jetcamera.features.videoplayer.screen.VideoPlayerScreenRoot
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf


@Serializable
data class VideoPlayerRoute(
    val videoPath: String,
)

@Serializable
data object HomeRoute


@Parcelize
@Serializable
data class ImageArgs(val bytes: ByteArray) : Parcelable


@Serializable
data class ImageViewerRoute(val args: ImageArgs) {
    companion object {
        val typeMap =
            mapOf(
                typeOf<ImageArgs>() to serializableType<ImageArgs>(),
            )

        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<ImageViewerRoute>(typeMap)
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    startRoute: Any = HomeRoute,
    navController: NavHostController,
    onShowSnackBar: (String) -> Unit = {}
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startRoute
    ) {
        composable<HomeRoute>(
        ) {
            HomeScreen(
                onImageCaptured = {
                    navController.navigate(ImageViewerRoute(ImageArgs(it.toByteArray())))
                },
                onShowSnackBar = onShowSnackBar,
                onVideoRecorded = {
                    navController.navigate(VideoPlayerRoute(it.path))
                }
            )
        }
        composable<ImageViewerRoute>(
            typeMap = ImageViewerRoute.typeMap,
        ) {
            ImageViewerScreenRoot(
                onShowSnackBar = onShowSnackBar,
                onImageSaved = { navController.popBackStack() }
            )
        }

        composable<VideoPlayerRoute> {
            VideoPlayerScreenRoot(
                onShowSnackBar = onShowSnackBar,
                onSavedVideo = { navController.popBackStack() }
            )
        }

    }

}


