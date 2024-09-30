Camera Selfie SDK is a simple Android SDK for taking selfies and viewing previously captured photos. It provides a consistent interface to handle camera access, photo storage, and user authentication. The SDK is built using Kotlin and Jetpack Compose.
Features
Take a Selfie: Uses the device's front camera to capture photos.
View Selfies: Allows users to view previously taken photos.
Biometric Authentication: Ensures photos are only accessible after user authentication.

Getting Started
Installation
To integrate PhotoSDK into your Android project, include the SDK as a module in your project or as a dependency if you are using a binary distribution.

Add the SDK module to your project:
dependencies {
    implementation(project(":photosdk"))
}

If distributing the SDK as an .aar file, include it in your libs folder and update your build.gradle.kts:
implementation(files("libs/photosdk.aar"))

Permissions
Your app needs to request camera and storage permissions for the SDK to work correctly. Make sure to declare the following permissions in your AndroidManifest.xml:
<uses-permission android:name="android.permission.CAMERA" />
