package com.rsetiapp.common.model.response

data class LoginRes(
    val wrappedList: List<LoginDetails>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String,
    val appCode: String
)

data class LoginDetails(
    val loginId: String,
    val userName: String
)

