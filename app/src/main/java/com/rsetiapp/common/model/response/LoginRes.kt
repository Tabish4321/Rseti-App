package com.rsetiapp.common.model.response

data class LoginRes(
    val wrappedList: List<LoginDetails>, // Replace Any with the appropriate type if wrappedList contains specific objects
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String
)

data class LoginDetails(
    val loginId: String,
    val userName: String
)

