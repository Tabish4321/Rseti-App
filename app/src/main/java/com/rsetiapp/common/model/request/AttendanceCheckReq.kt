package com.rsetiapp.common.model.request

data class AttendanceCheckReq(
    val appVersion: String,
    val batchId: String,
    val candidateId: String
)

