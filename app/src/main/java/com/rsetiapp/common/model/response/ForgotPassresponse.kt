package com.rsetiapp.common.model.response

data class ForgotPassresponse(
    val wrappedList: List<Any>, // Use a specific type if needed
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?

)
