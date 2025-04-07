package com.rsetiapp.common.model.response

data class SalaryRangeRes(

    val wrappedList: List<SalaryRange>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?

)
data class SalaryRange(
    val salaryRange: String,
    val salaryRangeId: String
)
