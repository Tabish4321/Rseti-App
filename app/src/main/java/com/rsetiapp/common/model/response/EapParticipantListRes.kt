package com.rsetiapp.common.model.response

data class EapParticipantListRes(
    val wrappedList: List<CandidateEapData> = emptyList(),
    val responseCode: Int,
    val responseDesc: String
)

data class CandidateEapData(
    val candidateEapId: Int,
    val candidateName: String,
    val dateOfBirth: String,
    val candidateId: String
)