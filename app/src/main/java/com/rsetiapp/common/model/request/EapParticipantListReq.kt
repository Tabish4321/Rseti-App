package com.rsetiapp.common.model.request

data class EapParticipantListReq(
    val appVersion: String,
    val login: String,
    val imeiNo: String,
    val eapId: Int
)
