package com.rsetiapp.common.model.response

import java.io.Serializable

data class CandidateDetail(
    val batchId: Int,
    val candidateId: String,
    val candidateName: String,
    val candidateProfilePic: String,
    val guardianName: String,
    val guardianNo: String,
    val mobileNo: String,
    val rollNo: Int,
    val statusI: String,
    val statusII: String,
    val statusIII: String,
    val statusIV: String,
    val statusV: String,
    val statusVI: String,
    val statusVII: String,
    val statusVIII: String,
    val followUpStatus: ArrayList<FollowUpStatus>?
) : Serializable

