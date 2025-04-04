package com.rsetiapp.common.model.response

data class AttendanceCandidateRes(
    val wrappedList: List<Candidate>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)


data class Candidate(
    val candidateName: String,
    val rollNo: Int,
    val mobileNo: String,
    val batchId: Int,
    val candidateId: String,
    val comAddress: String,
    val dateOfBirth: String,
    val emailId: String,
    val gender: String,
    val adhaarNo: String,
    val candidateProfilePic: String
)

