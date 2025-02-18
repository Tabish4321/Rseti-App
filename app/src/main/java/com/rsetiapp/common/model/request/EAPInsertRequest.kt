package com.rsetiapp.common.model.request

data class EAPInsertRequest(
    val appVersion: String,
    val orgId: String,
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
    val nextMonthExpectedAppNo: String,
    val programDesc: String,
    val photoPathOne: String?,
    val photoPathTwo: String?,
    val latitute: String?,
    val longitute: String?,
    val address: String?
)
