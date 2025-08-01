package com.rsetiapp.common.model.response


data class FacultyDetailsRes(
val wrappedList: List<FacultyAttendance>,
val responseCode: Int,
val responseDesc: String,
val responseMsg: String?,
val appCode: String?
)

data class FacultyAttendance(
    val langitude: String,
    val loginId: String,
    val aadhaarNo: String,
    val gender: String,
    val totalHours: String,
    val latitude: String,
    val emailId: String,
    val mobileNo: String,
    val batchId: Int,
    val checkIn: String,
    val dob: String,
    val facultyName: String,
    val batchRegNo: String,
    val checkOut: String,
    val radius: Int,
    val attendanceFlag: String
)


