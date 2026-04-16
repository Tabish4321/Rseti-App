package com.rsetiapp.common.model.request

data class OtpGenerateRequest(
    val appVersion: String,
    val login: String,
    val mobileNo: String,
    val imeiNo :String
)