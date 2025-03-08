package com.rsetiapp.common.model.response


data class CandidateDetailsRes(
    val wrappedList: List<CandidateData>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class CandidateData(
    val candidateName: String,
    val gender: String,
    val guardianName: String,
    val candidateAddress: String,
    val emailId: String,
    val mobileNo: String,
    val candidateId: String,
    val guardianMobileNo: String,
    val candidateProfilePic: String
)
