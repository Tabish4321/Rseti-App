package com.rsetiapp.common.compose.ui

import android.Manifest
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rsetiapp.R
import com.rsetiapp.common.compose.model.BatchSubmitRequest
import com.rsetiapp.common.compose.model.ImageItem
import com.rsetiapp.common.compose.model.VerificationItem
import com.rsetiapp.common.compose.viewmodel.BatchViewModel
import com.rsetiapp.core.util.AppUtil

/**
 * Created by Rishi Porwal
 */

object Keys {
    const val LOCATION_TYPE = "locationType"
    const val COORDINATES = "coordinates"
    const val SCHEME = "scheme"
    const val EDP = "edp"
    const val COURSE = "course"
    const val START_DATE = "startDate"
    const val END_DATE = "endDate"
    const val DST = "dst"
    const val COORDINATOR = "coordinator"
    const val CANDIDATES = "candidates"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchScreen(
    vm: BatchViewModel,
    onBack: () -> Unit = {},
    userId:String,
) {

    val state by vm.state.collectAsState()
    var selectedImages by remember { mutableStateOf<List<ImageItem>>(emptyList()) }

    val context = LocalContext.current
    val data = state.batchDetails

    val isBatchEnabled = state.selectedInstitute != null
    val isVerificationVisible = state.selectedBatch != null

    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate // fallback if error
        }
    }
    fun String?.orEmptySafe() = this ?: ""

    val verificationList = remember(data) {

        data?.let {

            mutableStateListOf(

                VerificationItem(
                    key = Keys.LOCATION_TYPE,
                    label = "Location Type",
                    value = it.programLocationType.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.COORDINATES,
                    label = "Coordinates",
                    value = it.location.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.SCHEME,
                    label = "Scheme & Sponsor",
                    value = it.schemeType.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.EDP,
                    label = "EDP",
                    value = it.edpType.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.COURSE,
                    label = "Course",
                    value = it.courseName.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.START_DATE,
                    label = "Start Date",
                    value = formatDate(it.startDate.orEmptySafe())
                ),

                VerificationItem(
                    key = Keys.END_DATE,
                    label = "End Date",
                    value = formatDate(it.endDate.orEmptySafe())
                ),

                VerificationItem(
                    key = Keys.DST,
                    label = "Domain Trainer",
                    value = it.dstName.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.COORDINATOR,
                    label = "Non Domain Trainer",
                    value = it.programCoordinator.orEmptySafe()
                ),

                VerificationItem(
                    key = Keys.CANDIDATES,
                    label = "Candidates",
                    value = it.candidateCount.orEmptySafe()
                )
            )

        } ?: mutableStateListOf()
    }

    val stateCode= AppUtil.getSavedEntityPreference(context).substringAfter("-").toIntOrNull()
    LaunchedEffect(Unit) {
        vm.load(stateCode!!)
    }

    fun getItem(key: String) = verificationList.find { it.key == key }

    fun logLongJson(tag: String, message: String) {
        val chunkSize = 1000
        var i = 0
        while (i < message.length) {
            Log.d(tag, message.substring(i, minOf(i + chunkSize, message.length)))
            i += chunkSize
        }
    }

    LaunchedEffect(state.isSaving) {
          if(state.isSaving){
              state.success.let {
                  Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
              }
              vm.resetSuccess()
              onBack()
          }
//        state.success.let {
//              Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//              vm.resetSuccess()
//              onBack()
//          }

    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
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

    LaunchedEffect(Unit) {
        fetchLocation { latitude, longitude ->
            lat = latitude
            lng = longitude
        }
    }

    Scaffold(

        topBar = {
            RsetiTopBar(
                title = "Batch Verification",
                onBackClick = onBack
            )
        },

        bottomBar = {

            if (isVerificationVisible) {
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {

                    Button(
                        onClick = {

                            if (state.isSaving) return@Button // 🔥 prevent double click

                            var hasError = false

                            verificationList.forEach {

                                if (it.answer == null) {
                                    Toast.makeText(context, "${it.label} select Yes/No", Toast.LENGTH_SHORT).show()
                                    hasError = true
                                    return@forEach
                                }

                                if (it.answer == "NO" && it.remark.isBlank()) {
                                    Toast.makeText(context, "${it.label} remark required", Toast.LENGTH_SHORT).show()
                                    hasError = true
                                    return@forEach
                                }
                            }

                            if (selectedImages.size != 2) {
                                Toast.makeText(context, "Upload exactly 2 images", Toast.LENGTH_SHORT).show()
                                hasError = true
                            }

                            if (hasError) return@Button


                            val request = BatchSubmitRequest(

                                batchId = state.selectedBatch?.batchRegNo.toString(),
                                instituteId = state.selectedInstitute?.instituteId.toString(),
                                loginId = userId,

                                verificationImageFirst = selectedImages.getOrNull(0)?.base64 ?: "",
                                verificationImageSecond = selectedImages.getOrNull(1)?.base64 ?: "",

                                locationType = getItem(Keys.LOCATION_TYPE)?.value ?: "",
                                locationTypeAnswer = getItem(Keys.LOCATION_TYPE)?.answer,
                                locationTypeRemark = getItem(Keys.LOCATION_TYPE)?.remark ?: "",

                                coordinates = getItem(Keys.COORDINATES)?.value ?: "",
                                coordinatesAnswer = getItem(Keys.COORDINATES)?.answer,
                                coordinatesRemark = getItem(Keys.COORDINATES)?.remark ?: "",
                                coordinatesValue = "${lat ?: ""},${lng ?: ""}",

                                scheme = getItem(Keys.SCHEME)?.value ?: "",
                                schemeAnswer = getItem(Keys.SCHEME)?.answer,
                                schemeRemark = getItem(Keys.SCHEME)?.remark ?: "",

                                edp = getItem(Keys.EDP)?.value ?: "",
                                edpAnswer = getItem(Keys.EDP)?.answer,
                                edpRemark = getItem(Keys.EDP)?.remark ?: "",

                                course = getItem(Keys.COURSE)?.value ?: "",
                                courseAnswer = getItem(Keys.COURSE)?.answer,
                                courseRemark = getItem(Keys.COURSE)?.remark ?: "",

                                startDate = getItem(Keys.START_DATE)?.value ?: "",
                                startDateAnswer = getItem(Keys.START_DATE)?.answer,
                                startDateRemark = getItem(Keys.START_DATE)?.remark ?: "",

                                endDate = getItem(Keys.END_DATE)?.value ?: "",
                                endDateAnswer = getItem(Keys.END_DATE)?.answer,
                                endDateRemark = getItem(Keys.END_DATE)?.remark ?: "",

                                dst = getItem(Keys.DST)?.value ?: "",
                                dstAnswer = getItem(Keys.DST)?.answer,
                                dstRemark = getItem(Keys.DST)?.remark ?: "",

                                coordinator = getItem(Keys.COORDINATOR)?.value ?: "",
                                coordinatorAnswer = getItem(Keys.COORDINATOR)?.answer,
                                coordinatorRemark = getItem(Keys.COORDINATOR)?.remark ?: "",

                                candidates = getItem(Keys.CANDIDATES)?.value ?: "",
                                candidatesAnswer = getItem(Keys.CANDIDATES)?.answer,
                                candidatesRemark = getItem(Keys.CANDIDATES)?.remark ?: ""
                            )

                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val jsonString = gson.toJson(request)

                            logLongJson("JSON_PRETTY", jsonString)
                            vm.save(request)
                        },

                        enabled = !state.isSaving,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp), // 🔥 better height

                        shape = RoundedCornerShape(16.dp),

                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        ),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.light_color),
                            contentColor = Color.White
                        )
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text("Submitting...")

                            } else {

                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Submit Verification",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Dropdown(
                    title = "Institute",
                    items = state.institutes,
                    selected = state.selectedInstitute?.instituteName,
                    itemLabel = { it.instituteName },
                    onSelect = vm::selectInstitute
                )
            }

            item {

                Box(
                    modifier = Modifier.alpha(if (isBatchEnabled) 1f else 0.5f)
                ) {
                    Dropdown(
                        title = "Batch",
                        items = state.batches,
                        selected = state.selectedBatch?.batchRegNo,
                        itemLabel = { it.tradeName +"("+ it.batchRegNo +")" },

                        onSelect = {
                            if (isBatchEnabled) vm.selectBatch(it)
                        }
                    )
                }
            }

            if (isVerificationVisible) {

                item {
                    VerificationHeader(lat, lng)
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        verificationList.forEach {
                            ComplianceItemCard(it)
                        }
                    }
                }

                item {
                    ImagePicker(
                        images = selectedImages,
                        onImagesSelected = {
                            selectedImages = it
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}






@Composable
fun SectionTitle(text: String) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color =  Color.Black,

        )
        //Spacer(modifier = Modifier.height(4.dp))

    }
}


//@Preview(showBackground = true)
//@Composable
//fun BatchScreenContentPreview() {
//
//    val context = LocalContext.current
//
//    val verificationList = remember {
//        mutableStateListOf(
//            VerificationItem("📍 Location Type", "On-Campus"),
//            VerificationItem("📌 Coordinates", "80,26dfgdgd ,drgedgttg"),
//            VerificationItem("📘 Scheme", "MoRD"),
//            VerificationItem("🎓 EDP", "Training"),
//            VerificationItem("🧵 Course", "Tailoring")
//        )
//    }
//
//    var selectedImages by remember { mutableStateOf<List<ImageItem>>(emptyList()) }
//    LazyColumn(
//        modifier = Modifier.padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//
//        item {
//            SectionTitle("Verification")
//            Column {
//                verificationList.forEach {
//                    ComplianceItemCard(it)
//                }
//
//            }
//        }
//
//        item {
//            SectionTitle("Geo-tag Photos")
//            ImagePicker(
//                images = selectedImages,
//                onImagesSelected = {
//                    selectedImages = it
//                }
//            )
//        }
//
//
//        item {
//            Button(onClick = {}) {
//                Text("Submit")
//            }
//        }
//    }
//}