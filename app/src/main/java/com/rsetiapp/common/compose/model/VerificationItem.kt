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