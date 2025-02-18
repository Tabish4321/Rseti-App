package com.rsetiapp.common.model.response

data class EAPInsertResponse(
    val wrappedList: List<Any>, // Change `Any` to specific type if needed
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String
)