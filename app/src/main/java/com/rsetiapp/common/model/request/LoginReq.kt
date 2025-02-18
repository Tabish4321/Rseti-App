package com.rsetiapp.common.model.request


data class LoginReq(
    val login: String,
    val password: String,
    val imeiNo: String,
    val appVersion: String,
    val deviceName: String
)

