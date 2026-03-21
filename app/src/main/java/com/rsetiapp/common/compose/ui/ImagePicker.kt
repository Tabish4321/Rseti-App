package com.rsetiapp.common.compose.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.rsetiapp.common.compose.model.ImageItem
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ImagePicker(
    onImagesSelected: (List<ImageItem>) -> Unit
) {

    val context = LocalContext.current
    val images = remember { mutableStateListOf<ImageItem>() }

    // 🔥 MULTIPLE PERMISSION
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        if (!granted) {
            Toast.makeText(context, "Permission required", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    fun fetchLocation(onResult: (Double?, Double?) -> Unit) {
        if (hasPermissions(context)) {
            getCurrentLocation(context, onResult)
        } else {
            requestPermission()
            onResult(null, null)
        }
    }

    fun processBitmap(bitmap: Bitmap) {

        fetchLocation { lat, lng ->

            val watermarked = createWatermarkedBitmap(bitmap, lat, lng)
            val uri = saveBitmapToCache(context, watermarked)

            val base64 = bitmapToBase64(watermarked)

            val time = SimpleDateFormat(
                "dd MMM yyyy hh:mm a",
                Locale.getDefault()
            ).format(Date())

            images.add(
                ImageItem(
                    uri = uri,
                    lat = lat,
                    lng = lng,
                    timestamp = time,
                    base64 = base64
                )
            )

            onImagesSelected(images)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { processBitmap(it) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            processBitmap(bitmap)
        }
    }

    Column {

        Text(" Geo-tag Photos")

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            Button(onClick = {
                if (hasPermissions(context)) {
                    galleryLauncher.launch("image/*")
                } else requestPermission()
            }) {
                Text("Gallery")
            }

            Button(onClick = {
                if (hasPermissions(context)) {
                    cameraLauncher.launch(null)
                } else requestPermission()
            }) {
                Text("Camera")
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(images) { item ->

                Box {
                    AsyncImage(
                        model = item.uri,
                        contentDescription = null,
                        modifier = Modifier.size(90.dp)
                    )

                    Text(
                        text = "❌",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable {
                                images.remove(item)
                                onImagesSelected(images)
                            }
                    )
                }
            }
        }
    }
}

fun createWatermarkedBitmap(
    original: Bitmap,
    lat: Double?,
    lng: Double?
): Bitmap {

    val result = original.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(result)

    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 40f
        isAntiAlias = true
        setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)
    }

    val time = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
        .format(Date())

    val location = if (lat != null && lng != null)
        "Lat:$lat Lng:$lng"
    else "Location unavailable"

    val text = "$time\n$location"

    canvas.drawText(text, 20f, result.height - 60f, paint)

    return result
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val byteArray = stream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {

    val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")

    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onResult: (Double?, Double?) -> Unit
) {
    val client = LocationServices.getFusedLocationProviderClient(context)

    client.lastLocation
        .addOnSuccessListener { location ->
            onResult(location?.latitude, location?.longitude)
        }
        .addOnFailureListener {
            onResult(null, null)
        }
}
fun hasPermissions(context: Context): Boolean {

    val camera = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    val location = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return camera && location
}