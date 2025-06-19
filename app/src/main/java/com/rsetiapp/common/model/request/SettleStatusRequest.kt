package com.rsetiapp.common.model.request

data class SettleStatusRequest(
    val appVersion: String,
    val login : String,
    val imeiNo : String
)
