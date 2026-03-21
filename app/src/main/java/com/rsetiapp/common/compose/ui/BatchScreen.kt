package com.rsetiapp.common.compose.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rsetiapp.common.compose.model.ImageItem
import com.rsetiapp.common.compose.model.VerificationItem
import com.rsetiapp.common.compose.viewmodel.BatchViewModel

/**
 * Created by Rishi Porwal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchScreen(
    vm: BatchViewModel,
    onBack: () -> Unit = {}
) {

    val state by vm.state.collectAsState()
    var selectedImages by remember { mutableStateOf<List<ImageItem>>(emptyList()) }

    val data = state.batchDetails
    val context = LocalContext.current

    val verificationList = remember(data) {
        data?.let {
            mutableStateListOf(
                VerificationItem("📍 Location Type", it.programLocationType),
                VerificationItem("📌 Coordinates", it.location),
                VerificationItem("📘 Scheme", it.schemeType),
                VerificationItem("🎓 EDP", it.edpType),
                VerificationItem("🧵 Course", it.courseName),
                VerificationItem("📅 Start", it.startDate),
                VerificationItem("📅 End", it.endDate),
                VerificationItem("👤 DST", it.dstName),
                VerificationItem("👨‍💼 Coordinator", it.programCoordinator),
                VerificationItem("👥 Candidates", it.candidateCount)
            )
        } ?: mutableStateListOf()
    }

    LaunchedEffect(Unit) {
        vm.load("UP")
    }

    Scaffold(
        topBar = {
            RsetiTopBar(
                title = "Batch Management Varification",
                onBackClick = onBack
            )
        }


    ) { padding ->


        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ✅ 1. Institute Dropdown
            item {
                SectionTitle("Select Institute")
                Dropdown(
                    title = "",
                    items = state.institutes,
                    selected = state.selectedInstitute?.instituteName,
                    onSelect = vm::selectInstitute
                )
            }

            // ✅ 2. Batch Dropdown
            item {
                SectionTitle("Select Batch")
                Dropdown(
                    title = "",
                    items = state.batches,
                    selected = state.selectedBatch?.batchRegNo,
                    onSelect = vm::selectBatch
                )
            }

                // 🔹 Batch Details
//                item {
//                    SectionTitle("Batch Details")
//                    BatchCard(data)
//                }

                // 🔹 Verification
                item {
                    SectionTitle("Verification")
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        verificationList.forEach {
                            VerificationRow(it)
                        }
                    }
                }

                // 🔹 Image Picker
                item {
                    SectionTitle("Geo-tag Photos")

                    ImagePicker { images ->
                        selectedImages = images
                    }
                }

                // 🔹 Submit Button
                item {
                    Button(
                        onClick = {

                            var hasError = false

                            verificationList.forEach {

                                if (it.answer == null) {
                                    Toast.makeText(
                                        context,
                                        "${it.label} select Yes/No",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    hasError = true
                                    return@forEach
                                }

                                if (it.answer == "NO" && it.remark.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "${it.label} remark required",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    hasError = true
                                    return@forEach
                                }
                            }

                            if (hasError) return@Button

                            // ✅ Prepare data
                            val verificationData = verificationList.map {
                                mapOf(
                                    "label" to it.label,
                                    "value" to it.value,
                                    "answer" to it.answer,
                                    "remark" to if (it.answer == "NO") it.remark else ""
                                )
                            }

                            val imageList = selectedImages.map { it.base64 }

                            // ✅ API call
                            vm.save(
//                                instituteId = state.selectedInstitute?.instituteId,
//                                batchId = state.selectedBatch?.batchId,
//                                verification = verificationData,
//                                images = imageList
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (state.isSaving) "Saving..." else "Submit")
                    }
                }
            }
        }
    }



//{
//    "instituteId": "101",
//    "batchId": "201",
//    "verification": [
//    {
//        "label": "Location Type",
//        "value": "On-Campus",
//        "answer": "YES",
//        "remark": ""
//    },
//    {
//        "label": "Coordinates",
//        "value": "80,26",
//        "answer": "NO",
//        "remark": "Wrong location"
//    }
//    ],
//    "images": [
//    "base64string1",
//    "base64string2"
//    ]
//}





@Composable
fun SectionTitle(text: String) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,

        )

        Spacer(modifier = Modifier.height(4.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun BatchScreenContentPreview() {

    val context = LocalContext.current

    val verificationList = remember {
        mutableStateListOf(
            VerificationItem("📍 Location Type", "On-Campus"),
            VerificationItem("📌 Coordinates", "80,26"),
            VerificationItem("📘 Scheme", "MoRD"),
            VerificationItem("🎓 EDP", "Training"),
            VerificationItem("🧵 Course", "Tailoring")
        )
    }

    var selectedImages by remember { mutableStateOf<List<ImageItem>>(emptyList()) }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            SectionTitle("Verification")
            Column {
                verificationList.forEach {
                    VerificationRow(it)
                }
            }
        }

        item {
            SectionTitle("Geo-tag Photos")
            ImagePicker { selectedImages = it }
        }

        item {
            Button(onClick = {}) {
                Text("Submit")
            }
        }
    }
}