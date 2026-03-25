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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontLoadingStrategy.Companion.Async
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.rsetiapp.common.compose.model.ImageItem
import org.jetbrains.annotations.Async
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.rsetiapp.R







@Composable
fun ImagePicker(
    images: List<ImageItem>,
    onImagesSelected: (List<ImageItem>) -> Unit) {

    val context = LocalContext.current

    val imageList = remember { mutableStateListOf<ImageItem>() }

    LaunchedEffect(images) {
        imageList.clear()
        imageList.addAll(images)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.all { it }) {
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

           // val watermarked = createWatermarkedBitmap(bitmap, lat, lng)
            val uri = saveBitmapToCache(context, bitmap)
            val base64 = bitmapToBase64(bitmap)

            val time = SimpleDateFormat(
                "dd MMM yyyy hh:mm a",
                Locale.getDefault()
            ).format(Date())

            imageList.add(
                ImageItem(
                    uri = uri,
                    lat = lat,
                    lng = lng,
                    timestamp = time,
                    base64 = base64
                )
            )
            onImagesSelected(imageList.toList())

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

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // 🔹 Header
        Text(
            text = "Upload 2 Geo-tagged Photos *",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        // 🔹 Slots Row (ONLY 2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            repeat(2) { index ->

                val item = images.getOrNull(index)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF5F5F5))
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {

                            // 👉 same logic (camera open)
                            if (images.size >= 2 && item == null) {
                                Toast.makeText(context, "Only 2 images allowed", Toast.LENGTH_SHORT).show()
                            } else {
                                if (hasPermissions(context)) {
                                    cameraLauncher.launch(null)
                                } else requestPermission()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (item == null) {

                        // 🔹 EMPTY SLOT UI
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "+",
                                style = MaterialTheme.typography.headlineMedium,
                                color = colorResource(id = R.color.light_color),
                            )

                            Text(
                                text = "Add Photo",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                    } else {

                        // 🔹 IMAGE UI
                        Box {
                            AsyncImage(
                                model = item.uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )

                            // 🔹 Overlay Geo Info
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(6.dp)
                            ) {

                                Text(
                                    text = "Lat: ${item.lat ?: "--"}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )

                                Text(
                                    text = "Lng: ${item.lng ?: "--"}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )

                                Text(
                                    text = item.timestamp ?: "",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            // 🔹 Remove Button
                            IconButton(
                                onClick = {
                                    imageList.remove(item)
                                    onImagesSelected(imageList.toList())
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {

                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {

                                    Text(
                                        "✕",
                                        color = Color.White,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 🔹 Helper Text
        Text(
            text = "Exactly 2 photos required",
            style = MaterialTheme.typography.labelSmall,
            color = if (images.size == 2) Color(0xFF2E7D32) else Color.Gray
        )
    }
}

//fun createWatermarkedBitmap(
//    original: Bitmap,
//    lat: Double?,
//    lng: Double?
//): Bitmap {
//
//    val result = original.copy(Bitmap.Config.ARGB_8888, true)
//    val canvas = Canvas(result)
//
//    val paint = Paint().apply {
//        color = Color.White.hashCode()
//        textSize = 42f
//        isAntiAlias = true
//        setShadowLayer(6f, 0f, 0f, Color.Black.hashCode())
//    }
//
//    val time = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
//        .format(Date())
//
//    val locationText = if (lat != null && lng != null) {
//        "Lat: %.5f  Lng: %.5f".format(lat, lng)
//    } else {
//        "Location unavailable"
//    }
//
//    val text = "$time\n$locationText"
//
//    canvas.drawText(text, 20f, result.height - 80f, paint)
//
//    return result
//}

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