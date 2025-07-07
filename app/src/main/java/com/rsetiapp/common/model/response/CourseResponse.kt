package com.rsetiapp.common.model.response

data class CourseResponse(
    val wrappedList: List<CourseItem>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?,
    val appCode: String?
)

data class CourseItem(
    val courseName: String,
    val courseCode: String
)

