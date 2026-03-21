package com.rsetiapp.common.compose.state

import com.rsetiapp.common.compose.model.BatchDetailsDto
import com.rsetiapp.common.compose.model.BatchDto
import com.rsetiapp.common.compose.model.InstituteDto

/**
 * Created by Rishi Porwal
 */
data class BatchUiState(

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val institutes: List<InstituteDto> = emptyList(),
    val batches: List<BatchDto> = emptyList(),
    val batchDetails: BatchDetailsDto? = null,

    val selectedInstitute: InstituteDto? = null,
    val selectedBatch: BatchDto? = null,

    val isYesSelected: Boolean? = null,
    val remarks: String = "",

    val lat: Double? = null,
    val lng: Double? = null,

    val error: String? = null,
    val success: String? = null
)