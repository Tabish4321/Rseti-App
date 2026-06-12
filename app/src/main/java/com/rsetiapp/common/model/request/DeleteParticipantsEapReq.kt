package com.rsetiapp.common.model.request

data class DeleteParticipantsEapReq(
    val appVersion: String,
    val login: String,
    val imeiNo: String,
    val candidateEapId: Int,

)
