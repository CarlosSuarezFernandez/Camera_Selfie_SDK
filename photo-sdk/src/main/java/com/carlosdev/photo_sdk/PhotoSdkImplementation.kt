package com.carlosdev.photo_sdk

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PhotoSDKImpl(private val context: Context) : PhotoSDK {

    private var isAuthenticated = false

    override fun takePhoto(onPhotoTaken: (Uri) -> Unit, onError: (String) -> Unit) {
        val intent = Intent(context, CameraActivity::class.java)
        context.startActivity(intent)
        CameraActivity.onPhotoTakenCallback = onPhotoTaken
        CameraActivity.onErrorCallback = onError
    }

    @Composable
    override fun accessPhotos(onPhotosFetched: (List<Uri>) -> Unit, onError: (String) -> Unit) {
        if (context !is PhotoGalleryActivity) {
            val intent = Intent(context, PhotoGalleryActivity::class.java)
            context.startActivity(intent)
        } else {
            authenticateUser(onSuccess = {
                isAuthenticated = true
                fetchPhotos(onPhotosFetched, onError)

            }, onError = { error ->
                onError(error)
            })
        }

    }

    private fun fetchPhotos(onPhotosFetched: (List<Uri>) -> Unit, onError: (String) -> Unit) {
        try {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )

            val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            val selectionArgs =
                arrayOf("%Pictures%")

            val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val photos = mutableListOf<Uri>()

            context.contentResolver.query(
                queryUri,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val photoUri = ContentUris.withAppendedId(queryUri, id)
                    photos.add(photoUri)
                }
            }

            if (photos.isNotEmpty()) {
                onPhotosFetched(photos)
            } else {
                onError("No photos found")
            }
        } catch (e: Exception) {
            onError("Error accessing photos: ${e.message}")
        }
    }


    @Composable
    override fun authenticateUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val biometricManager = BiometricManager.from(context)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                isAuthenticated = true
                ShowBiometricPrompt(onSuccess, onError)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> onError("Device doesn't have biometric hardware")

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> onError("Authentication hardware is unavailable")

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> onError("There is no biometric enrolled in this device")

            else -> onError("Unknown error")
        }
    }

    @Composable
    fun ShowBiometricPrompt(
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        val executor = remember { ContextCompat.getMainExecutor(context) }
        val biometricPrompt = remember {
            BiometricPrompt(context as FragmentActivity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onError(errString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("Auth failed")
                    }
                })
        }

        val promptInfo = remember {
            BiometricPrompt.PromptInfo.Builder().setTitle("Biometric authentication")
                .setSubtitle("Sign in using your biometric credential")
                .setNegativeButtonText("Cancel").build()
        }

        LaunchedEffect(Unit) {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}