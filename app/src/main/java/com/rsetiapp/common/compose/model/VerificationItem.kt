package com.rsetiapp.common.compose.model

import com.rsetiapp.core.util.AppUtil

/**
 * Created by Rishi Porwal
 */
data class VerificationItem(
    val key: String,
    val label: String,
    val value: String,
    var answer: String? = null,   // YES / NO
    var remark: String = ""
)

//data class BatchSubmitRequest(
//    val instituteId: String,
//    val batchId: String,
//    val verification: List<VerificationItem>,
//    val images: Map<String, String>
//)

data class BatchSubmitRequest(

    val batchId: String,
    val instituteId: String,
    val loginId: String = AppUtil.getSavedEntityPreference(context),

    val verificationImageFirst: String,
    val verificationImageSecond: String,

    val locationType: String,
    val locationTypeAnswer: String?,
    val locationTypeRemark: String,

    val coordinates: String,
    val coordinatesAnswer: String?,
    val coordinatesRemark: String,
    val coordinatesValue: String,

    val scheme: String,
    val schemeAnswer: String?,
    val schemeRemark: String,

    val edp: String,
    val edpAnswer: String?,
    val edpRemark: String,

    val course: String,
    val courseAnswer: String?,
    val courseRemark: String,

    val startDate: String,
    val startDateAnswer: String?,
    val startDateRemark: String,

    val endDate: String,
    val endDateAnswer: String?,
    val endDateRemark: String,

    val dst: String,
    val dstAnswer: String?,
    val dstRemark: String,

    val coordinator: String,
    val coordinatorAnswer: String?,
    val coordinatorRemark: String,

    val candidates: String,
    val candidatesAnswer: String?,
    val candidatesRemark: String
)