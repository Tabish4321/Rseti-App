package com.rsetiapp.common.compose.base

/**
 * Created by Rishi Porwal
 */
data class BaseResponse<T>(
    val wrappedList: T? = null,
    val responseCode: Int,
    val responseDesc: String
)
