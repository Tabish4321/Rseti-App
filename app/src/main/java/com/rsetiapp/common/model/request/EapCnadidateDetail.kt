package com.rsetiapp.common.model.request

data class EapCnadidateDetail(
    val appVersion: String,
    val login: String,
    val imeiNo: String,
    val stateCode: String,
    val eapId: Int,
    val candidateId: String,
    val candidateName: String,
    val gender: String,
    val candidateAddress: String,
    val mobileNo: String,
    val guardianName: String,
    val guardianMobileNo: String,
    val candidateImage: String,
    val dob: String,
    val courseCode: String,
    val orgId: Int,
    val hrId: String,
    val entityCode: String
)
