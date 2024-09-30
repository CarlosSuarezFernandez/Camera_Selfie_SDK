package com.carlosdev.photo_sdk

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CameraActivity : ComponentActivity() {

    companion object {
        var onPhotoTakenCallback: ((Uri) -> Unit)? = null
        var onErrorCallback: ((String) -> Unit)? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraScreen(
                onPhotoTaken = { uri ->
                    onPhotoTakenCallback?.invoke(uri)
                    finish()
                },
                onError = { errorMessage ->
                    onErrorCallback?.invoke(errorMessage)
                }
            )
        }
    }
}