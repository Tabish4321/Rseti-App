package com.rsetiapp.common.model.request

data class OtpGenerateRequest(
    val appVersion: String,
    val login: String,
    val otp: String,
    val mobileNo: String
)