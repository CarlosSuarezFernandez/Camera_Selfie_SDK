package com.carlosdev.photo_sdk

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class PhotoGalleryActivity : AppCompatActivity() {

    private val photoSdk = PhotoSDKImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var photos by remember { mutableStateOf<List<Uri>>(emptyList()) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            photoSdk.accessPhotos(
                onPhotosFetched = { uris ->
                    photos = uris
                },
                onError = { error ->
                    errorMessage = error
                }
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!)
            } else {
                PhotosScreen(photoUris = photos, onError = { error ->
                    errorMessage = error
                })
            }
        }
    }


}