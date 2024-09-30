package com.carlosdev.photo_sdk

import android.net.Uri
import androidx.compose.runtime.Composable
import java.io.File

interface PhotoSDK {
    fun takePhoto(onPhotoTaken: (Uri) -> Unit, onError: (String) -> Unit)
    @Composable
    fun accessPhotos(onPhotosFetched: (List<Uri>) -> Unit, onError: (String) -> Unit)
    @Composable
    fun authenticateUser(onSuccess: () -> Unit, onError: (String) -> Unit)
}