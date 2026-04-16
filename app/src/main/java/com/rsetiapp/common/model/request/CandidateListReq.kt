package com.rsetiapp.common.model.request

data class CandidateListReq(
    val appVersion: String,
    val batchId: String, val imeiNo :String,
    val login: String
)