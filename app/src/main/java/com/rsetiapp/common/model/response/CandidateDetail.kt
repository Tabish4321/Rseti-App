package com.rsetiapp.common.model.response

import java.io.Serializable

data class CandidateDetail(
    val id: String,
    val name: String,
    val rollNumber: String,
    val contactNumber: String,
    val careOf: String,
    val profileImage: Int,
    val followUpStatus: ArrayList<FollowUpStatus>
) : Serializable

