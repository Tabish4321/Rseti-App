package com.rsetiapp.common

import com.rsetiapp.common.model.response.StateDataResponse
import com.rsetiapp.common.model.request.StateListReq
import com.rsetiapp.common.model.request.BlockReq
import com.rsetiapp.common.model.request.CandidateDetailsReq
import com.rsetiapp.common.model.request.CandidateSearchReq
import com.rsetiapp.common.model.request.DistrictReq
import com.rsetiapp.common.model.request.EAPInsertRequest
import com.rsetiapp.common.model.request.EapAutofetchReq
import com.rsetiapp.common.model.request.EapListReq
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
import com.rsetiapp.common.model.response.CandidateDetailsRes
import com.rsetiapp.common.model.response.CandidateSearchResp
import com.rsetiapp.common.model.response.EAPInsertResponse
import com.rsetiapp.common.model.response.EapAutoFetchRes
import com.rsetiapp.common.model.response.EapListResponse
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

    suspend fun candidateSearchListAPI(candidateSearchReq: CandidateSearchReq): Flow<Resource<out CandidateSearchResp>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.candidateSearchListAPI(candidateSearchReq)
        }
    }

    suspend fun candidateDetailsAPI(candidateDetailsReq: CandidateDetailsReq): Flow<Resource<out CandidateDetailsRes>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.candidateDetailsAPI(candidateDetailsReq)
        }
    }
    suspend fun eapDetailsAPI(eapListReq: EapListReq): Flow<Resource<out EapListResponse>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.eapDetailsAPI(eapListReq)
        }
    }

}