    package com.rsetiapp.common.model.response

    data class SettleStatusResponse(

        val wrappedList: List<SettlementStatus>,
        val responseCode: Int,
        val responseDesc: String,
        val responseMsg: String?,
        val appCode: String?
    )

    data class SettlementStatus(
        val statusId: Int,
        val status: String
    )

