package com.rsetiapp.common.model.response


data class AttendanceBatchRes(
    val wrappedList: List<AttendanceBatch>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class AttendanceBatch(
    val batchName: String,
    val batchRegNumber: String,
    val batchCode: Int
)
