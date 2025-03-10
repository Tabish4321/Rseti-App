package com.rsetiapp.common.model.response


data class CandidateSearchResp(
    val wrappedList: List<CandidateSearchData>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class CandidateSearchData(
    val candidateName: String,
    val candidateId: String,
    val candidateProfilePic: String?
)
