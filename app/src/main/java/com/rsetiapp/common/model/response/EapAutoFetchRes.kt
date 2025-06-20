package com.rsetiapp.common.model.response

data class EapAutoFetchRes(
    val wrappedList: List<WrappedItem>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)


data class WrappedItem(
    val instituteData: List<Institute>,
    val autoFetchData: List<AutoFetch>
)

data class Institute(
    val instituteCode: Int,
    val instituteName: String
)

data class AutoFetch(
    val ageLimit: Int,
    val orgName: String,
    val orgCode: String,
    val designation: String,
    val officialName: String
)
