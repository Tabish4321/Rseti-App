package com.rsetiapp.common.model.response

data class BatchListResponse(
    val wrappedList: List<Batch>,
    val responseCode: Int,
    val responseDesc: String?,
    val responseMsg: String?,
    val appCode: String?
)
