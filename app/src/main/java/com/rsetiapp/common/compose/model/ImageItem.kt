package com.rsetiapp.common.compose.model

import android.net.Uri

/**
 * Created by Rishi Porwal
 */
data class ImageItem(
    val uri: Uri,
    val lat: Double?,
    val lng: Double?,
    val timestamp: String,
    val base64: String


)