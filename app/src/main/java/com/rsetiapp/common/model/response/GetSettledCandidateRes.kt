package com.rsetiapp.common.model.response

import java.io.Serializable

data class GetSettledCandidateRes(
    val wrappedList: List<SettledCandidate>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class SettledCandidate(
    val settledOn: String,
    val candidateName: String,
    val verificationStatus: String,
    val rollNo: Int,
    val mobileNo: String,
    val batchRegNo: String,
    val candidateId: String,
    val guardianMobileNo: String
)


