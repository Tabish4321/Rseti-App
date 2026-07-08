package com.rsetiapp.common.model.request

data class EAPInsertRequest(
    val appVersion: String,
    val login: String,
    val imeiNo: String,
    val stateCode: String,
    val eapId: Int,
    val programeDate: String,
    val orgId: Int,
    val hrId: String,
    val entityCode: String,
    val instituteId: String,
    val totalParticipants: String,
    val nameOfOrg: String,
    val officialName: String,
    val designation: String,
    val programCode: String,
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
    val address: String
)

