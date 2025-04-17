package com.rsetiapp.common.model.request

data class AttendanceInsertReq(
     val imeiNo :String,
      val login: String,
    val appVersion: String,
    val batchId: String,
    val candidateId: String,
    val attandanceDate: String,
    val attandanceFlag: String,
    val checkIn: String,
    val checkOut: String,
    val totalHours: String,
    val candidateName: String
)
