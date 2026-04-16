package com.rsetiapp.common.model.request

data class ValidateOtpReq(
    val appVersion :String,
    val mobileNo :String,
    val imeiNo :String,
    val otp :String,
    val login:String
)
