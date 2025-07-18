package com.rsetiapp.common.model.request

data class FaceCheckReq(
    val appVersion: String,
    val isFaceRegistered: String,
    val login: String
)
