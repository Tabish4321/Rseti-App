package com.rsetiapp.core.domain.model.response

import java.io.Serializable


data class RefreshTokenResponse(
    val data: RefreshTokenResponseData,
    val message: String, val success: Boolean
) : Serializable

data class RefreshTokenResponseData(
    val accessToken: String,
    val refreshToken: String,
)
