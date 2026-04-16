package com.rsetiapp.common.model.request

data class InsertFacultyReq(
    val appVersion: String,
    val batchId: String,
    val attandanceDate: String,
    val attandanceFlag: String,
    val checkIn: String,
    val checkOut: String,
    val totalHours: String,
    val login: String,
    val imeiNo: String,
    val orgId: String,
    val hrId: String,
    val entityCode: String
)
