package com.rsetiapp.common

import com.rsetiapp.R
import com.rsetiapp.common.model.request.BatchListReq
import com.rsetiapp.common.model.response.StateDataResponse
import com.rsetiapp.common.model.request.StateListReq
import com.rsetiapp.common.model.request.BlockReq
import com.rsetiapp.common.model.request.DistrictReq
import com.rsetiapp.common.model.request.EAPInsertRequest
import com.rsetiapp.common.model.request.EapAutofetchReq
import com.rsetiapp.common.model.request.FogotPaasReq
import com.rsetiapp.common.model.request.GramPanchayatReq
import com.rsetiapp.common.model.request.VillageReq
import com.rsetiapp.common.model.response.BlockResponse
import com.rsetiapp.common.model.response.DistrictResponse
import com.rsetiapp.common.model.response.VillageResponse
import com.rsetiapp.common.model.response.grampanchayatResponse
import com.rsetiapp.common.model.request.FormRequest
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.common.model.request.OtpGenerateRequest
import com.rsetiapp.common.model.response.Batch
import com.rsetiapp.common.model.response.BatchListResponse
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.common.model.response.EAPInsertResponse
import com.rsetiapp.common.model.response.EapAutoFetchRes
import com.rsetiapp.common.model.response.FollowUpStatus
import com.rsetiapp.common.model.response.ForgotPassresponse
import com.rsetiapp.common.model.response.FormResponse
import com.rsetiapp.common.model.response.LoginRes
import com.rsetiapp.common.model.response.OtpGenerateResponse
import com.rsetiapp.common.model.response.ProgramResponse
import com.rsetiapp.core.data.local.database.AppDatabase
import com.rsetiapp.core.data.remote.AppLevelApi
import com.rsetiapp.core.di.AppModule
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.networkBoundResourceWithoutDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


class CommonRepository @Inject constructor(
    @AppModule.PreLoginAppLevelApi private val appLevelApi: AppLevelApi,
    private val database: AppDatabase
    ){



    suspend fun getLoginAPI(loginReq: LoginReq): Flow<Resource<out LoginRes>>{
        return networkBoundResourceWithoutDb {

            appLevelApi.getLoginAPI(loginReq)
        }
    }

    suspend fun getFormAPI(appVersion : String, login :String): Flow<Resource<out FormResponse>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getFormAPI(FormRequest(appVersion,login))
        }
    }
    suspend fun getStateListApi(appVersion: String): Flow<Resource<out StateDataResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getStateListAPI(StateListReq(appVersion))
        }
    }
    suspend fun getDistrictListApi(stateCode: String,appVersion: String): Flow<Resource<out DistrictResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getDistrictListAPI(DistrictReq(stateCode, appVersion))
        }
    }

    suspend fun getBlockListApi(districtCode: String,appVersion: String): Flow<Resource<out BlockResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getBlockListAPI(BlockReq(districtCode, appVersion))
        }
    }
    suspend fun getGPListApi(blockCode: String,appVersion: String): Flow<Resource<out grampanchayatResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getGpListAPI(GramPanchayatReq(blockCode, appVersion))
        }
    }

    suspend fun getVillageListApi(gpCode: String,appVersion: String): Flow<Resource<out VillageResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getVillageListAPI(VillageReq(gpCode, appVersion))
        }
    }

    suspend fun getProgramListAPI(appVersion: String): Flow<Resource<out ProgramResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getProgramListAPI(StateListReq( appVersion))
        }
    }

    suspend fun getEapAutoFetchListAPI(login : String, appVersion: String): Flow<Resource<out EapAutoFetchRes>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getEapAutoFetchListAPI(EapAutofetchReq(login, appVersion))
        }
    }

    suspend fun insertEAPAPI(eapInsertRequest: EAPInsertRequest): Flow<Resource<out EAPInsertResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.insertEAPAPI(eapInsertRequest)
        }
    }


    suspend fun generateOtpAPI(otpGenerateRequest: OtpGenerateRequest): Flow<Resource<out OtpGenerateResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.generateOtpAPI(otpGenerateRequest)
        }
    }

    suspend fun forgetPasswordAPI(fogotPaasReq: FogotPaasReq): Flow<Resource<out ForgotPassresponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.forgetPasswordAPI(fogotPaasReq)
        }
    }

    suspend fun getBatchAPI(appVersion : String): Flow<Resource<out BatchListResponse>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getFollowUpBatchListAPI(BatchListReq(appVersion) )
        }
    }

    suspend fun getCandidateAPI(appVersion : String, login :String, batchId: String): Flow<Resource<out List<CandidateDetail>>> {
        val status  = ArrayList<FollowUpStatus>().apply {
            add(FollowUpStatus("S1", "Settled"))
            add(FollowUpStatus("S2", "Settled"))
            add(FollowUpStatus("S3", "Settled"))
            add(FollowUpStatus("S4", "Settled"))
            add(FollowUpStatus("S5", "Not Settled"))
            add(FollowUpStatus("S6", "Not Settled"))
            add(FollowUpStatus("S7", "Not Settled"))
            add(FollowUpStatus("S8", "Not Settled"))
        }

        val candidateList = listOf(
            CandidateDetail("1", "Candidate 1","1", "7895185107", "Father 1", R.drawable.person, status),
            CandidateDetail("2", "Candidate 2","2", "7895185107", "Father 2", R.drawable.person, status),
            CandidateDetail("3", "Candidate 3","3", "7895185107", "Father 3", R.drawable.person, status),
            CandidateDetail("4", "Candidate 4","4", "7895185107", "Father 4", R.drawable.person, status),
            CandidateDetail("5", "Candidate 5","5", "7895185107", "Father 5", R.drawable.person, status),
            CandidateDetail("6", "Candidate 6","6", "7895185107", "Father 6", R.drawable.person, status),
            CandidateDetail("7", "Candidate 7","7", "7895185107", "Father 7", R.drawable.person, status),
            CandidateDetail("8", "Candidate 8","8", "7895185107", "Father 8", R.drawable.person, status),
            CandidateDetail("9", "Candidate 9","9", "7895185107", "Father 9", R.drawable.person, status),
            CandidateDetail("10", "Candidate 10","10", "7895185107", "Father 10", R.drawable.person, status),
            CandidateDetail("11", "Candidate 11","11", "7895185107", "Father 11", R.drawable.person, status),
            CandidateDetail("12", "Candidate 12","12", "7895185107", "Father 12", R.drawable.person, status)
        )

        return MutableStateFlow<Resource<out List<CandidateDetail>>>(Resource.Success(candidateList))
    }
}