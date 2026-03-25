package com.rsetiapp.common.compose.model

/**
 * Created by Rishi Porwal
 */
data class VerificationItem(
    val label: String,
    val value: String,
    var answer: String? = null,   // YES / NO
    var remark: String = ""
)

data class BatchSubmitRequest(
    val instituteId: String,
    val batchId: String,
    val verification: List<VerificationItem>,
    val images: Map<String, String>
)