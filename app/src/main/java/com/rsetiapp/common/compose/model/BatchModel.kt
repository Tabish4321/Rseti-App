package com.rsetiapp.common.compose.model

/**
 * Created by Rishi Porwal
 */
data class InstituteDto(
    val instituteId: String,
    val instituteName: String
)

data class BatchDto(
    val batchId: String,
    val batchRegNo: String,
    val edpName: String,
    val tradeName: String,
    val startDate: String,
    val endDate: String,
    val candidateCount: String
)

data class BatchDetailsDto(
    val batchId: String,
    val batchRegNo: String,
    val programLocationType: String,
    val location: String,
    val schemeType: String,
    val edpType: String,
    val courseName: String,
    val startDate: String,
    val endDate: String,
    val dstName: String,
    val programCoordinator: String,
    val candidateCount: String
)

