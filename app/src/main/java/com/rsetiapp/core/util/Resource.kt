package com.rsetiapp.core.util

import com.rsetiapp.core.domain.model.response.BaseErrorResponse

sealed class Resource<T>(
    val data: T?= null,
    val error: BaseErrorResponse? = null
){
    class Success<T>(data: T?): Resource<T>(data)
    class Loading<T>(data:T?=null) : Resource<T>(data)
    class Error<T>(errorResponse: BaseErrorResponse, data:T?=null): Resource<T>(data, errorResponse)
}
