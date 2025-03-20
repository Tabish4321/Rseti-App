package com.rsetiapp.common.model.response

import java.io.Serializable

data class CandidateDetail(
    val batchId: Int? = 0,
    val candidateId: String? = "",
    val candidateName: String? = "",
    val candidateProfilePic: String? = "",
    val guardianName: String? = "",
    val guardianNo: String? = "",
    val mobileNo: String? = "",
    val quarter1: String? = "",
    val quarter2: String? = "",
    val quarter3: String? = "",
    val quarter4: String? = "",
    val quarter5: String? = "",
    val quarter6: String? = "",
    val quarter7: String? = "",
    val quarter8: String? = "",
    val rollNo: Int? = 0,
    val sattleStatus: String? = ""
) : Serializable

