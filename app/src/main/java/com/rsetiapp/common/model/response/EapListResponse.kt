package com.rsetiapp.common.model.response

data class EapListResponse(
    val wrappedList: List<EapList>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class EapList(
    val eapID: String,
    val eapName: String,
    val monthYear: String,
    val eapAddress: String,
    val status: String
)
