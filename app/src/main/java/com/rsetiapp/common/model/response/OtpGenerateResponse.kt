package com.rsetiapp.common.model.response

data class OtpGenerateResponse(
    val wrappedList: List<Any>, // Use a specific type if needed
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?
)
