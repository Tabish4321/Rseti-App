package com.rsetiapp.core.domain.model.response

data class BaseErrorResponse(
    var code: Int = 0,
    var message: String = "Something went wrong!",
    var success: Boolean,
    var data: Any
)
