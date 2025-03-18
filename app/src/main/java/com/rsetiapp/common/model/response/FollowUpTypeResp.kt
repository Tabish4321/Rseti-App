package com.rsetiapp.common.model.response

data class FollowUpTypeResp(
    val appCode: Any,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: Any,
    val wrappedList: List<FollowUpType>
)

data class FollowUpType(
    val followUpTypeName: String,
    val followupTypeId: String
)