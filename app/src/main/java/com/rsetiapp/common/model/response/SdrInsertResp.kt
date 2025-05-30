package com.rsetiapp.common.model.response



data class SdrInsertResp(
    val wrappedList: List<Any>,
    val responseCode: Int,
    val responseDesc: String
)
