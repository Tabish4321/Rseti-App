package com.rsetiapp.common.model.response

data class EapAutoFetchRes(
    val wrappedList: List<Organization>,  // List of organization objects
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String? // Nullable since it can be null
)

data class Organization(
    val orgName: String,
    val orgCode: String,
    val instituteCode: String,
    val designation: String,
    val officialName: String,
    val instituteName: String,
    val ageLimit: Int
)
