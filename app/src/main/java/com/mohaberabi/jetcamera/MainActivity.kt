package com.mohaberabi.jetcamera

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import com.mohaberabi.jetcamera.core.AppNavHost
import com.mohaberabi.jetcamera.core.presentation.theme.JetCameraTheme
import com.mohaberabi.jetcamera.core.util.hasAllowedCameraPermission
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val launcher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                ) {

                }
            LaunchedEffect(
                key1 = true,
            ) {

                launcher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            }
            JetCameraTheme {
                if (!hasAllowedCameraPermission()) {
                    AppPlaceHolder()
                } else {
                    val hostState = SnackbarHostState()
                    val snackBarScope = rememberCoroutineScope()
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(hostState = hostState)
                        }
                    ) { innerPadding ->
                        val navController = rememberNavController()
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding),
                            onShowSnackBar = { message ->
                                snackBarScope.launch {
                                    hostState.showSnackbar(message)
                                }
                            }
                        )

                    }
                }

            }
        }
    }
}

@Composable
fun AppPlaceHolder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Please allow access to camera in order to use the app",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
