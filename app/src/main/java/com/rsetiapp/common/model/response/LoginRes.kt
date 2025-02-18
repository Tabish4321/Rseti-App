package com.rsetiapp.common.model.response

data class LoginRes(
    val wrappedList: List<Any>, // Replace Any with the appropriate type if wrappedList contains specific objects
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String
)

