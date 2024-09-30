package com.carlosdev.testappsdk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carlosdev.photo_sdk.PhotoSDK
import com.carlosdev.photo_sdk.PhotoSDKImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

class MainActivity : AppCompatActivity() {

    private val photoSdk by lazy { PhotoSDKImpl(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionRequestScreen()
            TestSdkScreen(photoSdk)
        }
    }
}


@Composable
fun TestSdkScreen(sdk: PhotoSDK) {
    var photoFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var buttonPhotoClicked by remember { mutableStateOf(false) }
    var buttonAccessPhotosClicked by remember { mutableStateOf(false) }
    var showToastPhoto by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {

        Button(
            onClick = {
                buttonPhotoClicked = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (buttonPhotoClicked) {
                sdk.takePhoto(
                    onPhotoTaken = { uri ->
                        showToastPhoto = true
                        buttonPhotoClicked = false
                        //
                    },
                    onError = { error ->
                        errorMessage = error
                        buttonPhotoClicked = false
                    }
                )
            }
            if (showToastPhoto) {
                Toast.makeText(LocalContext.current, "Photo taken", Toast.LENGTH_SHORT).show()
                showToastPhoto = false
            }
            Text(text = "Take photo")
        }

        Button(
            onClick = {
                buttonAccessPhotosClicked = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (buttonAccessPhotosClicked) {
                sdk.accessPhotos(
                    onPhotosFetched = { uris ->
                        photoFiles = uris.map { uri ->
                            File(uri.path!!)
                        }
                        buttonAccessPhotosClicked = false
                    },
                    onError = { error ->
                        errorMessage = error
                        buttonAccessPhotosClicked = false
                    }
                )
                buttonAccessPhotosClicked = false
            }
            Text(text = "Access Photos")
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestScreen() {
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}