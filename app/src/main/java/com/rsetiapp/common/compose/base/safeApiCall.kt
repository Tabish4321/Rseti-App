package com.rsetiapp.common.compose.base

/**
 * Created by Rishi Porwal
 */

suspend fun <T> safeApiCall(apiCall: suspend () -> BaseResponse<T>): ApiResult<T> {
    return try {
        val response = apiCall()

        if (response.responseCode == 200 && response.wrappedList != null) {
            ApiResult.Success(response.wrappedList)
        } else {
            ApiResult.Error(response.responseDesc)
        }

    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: "Something went wrong")
    }
}