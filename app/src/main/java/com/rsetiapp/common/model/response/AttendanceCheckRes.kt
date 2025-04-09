package com.rsetiapp.common.model.response

data class AttendanceCheckRes(
    val wrappedList: List<AttendanceData>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class AttendanceData(
    val checkIn: String,
    val totalHours: String,
    val lattitude: String,
    val checkOut: String,
    val radius: Int,
    val attendanceFlag: String,
    val longitude: String
)