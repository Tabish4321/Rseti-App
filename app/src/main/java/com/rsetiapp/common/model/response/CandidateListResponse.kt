package com.rsetiapp.common.model.response

data class CandidateListResponse(
    val appCode: Any?,
    val responseCode: Int?,
    val responseDesc: String,
    val responseMsg: Any?,
    val wrappedList: List<CandidateDetail>
)