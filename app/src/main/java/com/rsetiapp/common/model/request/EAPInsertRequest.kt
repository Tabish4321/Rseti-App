package com.rsetiapp.common.model.request

data class EAPInsertRequest(
    val imeiNo :String,
     val login: String,
     val appVersion: String,
    val orgId: String,
    val eapId: String,
    val instituteId: String,
    val programeDate: String,
    val totalParticipants: String,
    val nameOfOrg: String,
    val officialName: String,
    val designation: String,
    val programCode: String,
    val stateCode: String,
    val districtCode: String,
    val blockCode: String,
    val gpCode: String,
    val villageCode: String,
    val generatedApplicationNo: String,
    val programDesc: String,
    val photoPathOne: String,
    val photoPathTwo: String,
    val latitute: String,
    val longitute: String,
    val candidateDetails: List<Candidate>
)

data class Candidate(
    val candidateId: String,
    val candidateName: String,
    val gender: String,
    val guardianName: String,
    val guardianMobileNo: String,
    val candidateAddress: String,
    val mobileNo: String,
    val dob: String,
    val candidateImage: String,
    val courseCode: String,
)
