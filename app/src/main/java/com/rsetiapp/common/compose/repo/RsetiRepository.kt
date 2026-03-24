package com.rsetiapp.common.compose.repo

import com.rsetiapp.common.compose.base.ApiResult
import com.rsetiapp.common.compose.base.safeApiCall
import com.rsetiapp.common.compose.model.BatchSubmitRequest
import com.rsetiapp.common.model.response.SdrListResp
import com.rsetiapp.core.data.remote.AppLevelApi
import com.rsetiapp.core.di.AppModule
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Created by Rishi Porwal
 */
class RsetiRepository @Inject constructor(@AppModule.PostLoginAppLevelApi  private val api: AppLevelApi) {

    suspend fun getInstitutes(stateCode: Int) =
        safeApiCall { api.getInstitutes(stateCode) }

    suspend fun getBatches(instituteId: String) =
        safeApiCall { api.getBatches(instituteId) }

    suspend fun getBatchDetails(batchId: String, instituteId: String) =
        safeApiCall { api.getBatchDetails(batchId, instituteId) }


    suspend fun submitBatch(request: BatchSubmitRequest) = safeApiCall { api.submitBatchVerification(request) }



}