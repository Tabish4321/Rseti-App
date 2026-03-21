package com.rsetiapp.common.compose.repo

import com.rsetiapp.common.compose.base.ApiResult
import com.rsetiapp.common.compose.base.safeApiCall
import com.rsetiapp.core.data.remote.AppLevelApi
import com.rsetiapp.core.di.AppModule
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Created by Rishi Porwal
 */
class RsetiRepository @Inject constructor(@AppModule.PostLoginAppLevelApi  private val api: AppLevelApi) {

    suspend fun getInstitutes(stateCode: String) =
        safeApiCall { api.getInstitutes(stateCode) }

    suspend fun getBatches(instituteId: String) =
        safeApiCall { api.getBatches(instituteId) }

    suspend fun getBatchDetails(batchId: String, instituteId: String) =
        safeApiCall { api.getBatchDetails(batchId, instituteId) }


    suspend fun saveBatchInspection(
        isCorrect: Boolean,
        remarks: String,
        lat: Double?,
        lng: Double?
    ): ApiResult<String> {

        delay(1500) // simulate network

        return if (!isCorrect && remarks.isBlank()) {
            ApiResult.Error("Remarks required")
        } else {
            ApiResult.Success("Saved Successfully")
        }
    }
}