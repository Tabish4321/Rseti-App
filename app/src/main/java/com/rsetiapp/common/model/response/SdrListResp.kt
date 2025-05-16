package com.rsetiapp.common.model.response



data class SdrListResp(
    val wrappedList: List<VisitData>,
    val responseCode: Int,
    val responseDesc: String
)
data class VisitData(
    val month: Int,
    val sdrVisitStatus: String? = null,
    val lattitude: String,
    val longitude: String,
    val radius: Int,
    val finYear: String,
    val instituteId: Int,
    val instituteName: String
)
