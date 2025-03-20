package com.rsetiapp.common.model.response

data class FollowUpStatusResp(
    val appCode: Any,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: Any,
    val wrappedList: List<FollowUpStatus>
)

data class FollowUpStatus(
    val status: String,
    val statusId: Int
)