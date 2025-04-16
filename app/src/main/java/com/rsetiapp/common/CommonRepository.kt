package com.rsetiapp.common

import com.rsetiapp.core.uidai.ekyc.UidaiKycRequest
import com.rsetiapp.core.uidai.ekyc.UidaiResp
import com.rsetiapp.common.model.request.AttendanceBatchReq
import com.rsetiapp.common.model.request.AttendanceCandidateReq
import com.rsetiapp.common.model.request.AttendanceCheckReq
import com.rsetiapp.common.model.request.AttendanceInsertReq
import com.rsetiapp.common.model.request.BankIFSCSearchReq
import com.rsetiapp.common.model.request.BatchListReq
import com.rsetiapp.common.model.response.StateDataResponse
import com.rsetiapp.common.model.request.StateListReq
import com.rsetiapp.common.model.request.BlockReq
import com.rsetiapp.common.model.request.CandidateListReq
import com.rsetiapp.common.model.request.CandidateDetailsReq
import com.rsetiapp.common.model.request.CandidateSearchReq
import com.rsetiapp.common.model.request.DistrictReq
import com.rsetiapp.common.model.request.EAPInsertRequest
import com.rsetiapp.common.model.request.EapAutofetchReq
import com.rsetiapp.common.model.request.EapListReq
import com.rsetiapp.common.model.request.FogotPaasReq
import com.rsetiapp.common.model.request.FollowUpInsertReq
import com.rsetiapp.common.model.request.FollowUpTypeReq
import com.rsetiapp.common.model.request.GramPanchayatReq
import com.rsetiapp.common.model.request.VillageReq
import com.rsetiapp.common.model.response.BlockResponse
import com.rsetiapp.common.model.response.DistrictResponse
import com.rsetiapp.common.model.response.VillageResponse
import com.rsetiapp.common.model.response.grampanchayatResponse
import com.rsetiapp.common.model.request.FormRequest
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.common.model.request.OtpGenerateRequest
import com.rsetiapp.common.model.request.SalaryRangeReq
import com.rsetiapp.common.model.response.AttendanceBatchRes
import com.rsetiapp.common.model.response.AttendanceCandidateRes
import com.rsetiapp.common.model.response.BankIFSCSearchRes
import com.rsetiapp.common.model.response.Batch
import com.rsetiapp.common.model.response.AttendanceCheckRes
import com.rsetiapp.common.model.response.AttendanceInsertRes
import com.rsetiapp.common.model.response.BatchListResponse
import com.rsetiapp.common.model.response.CandidateListResponse
import com.rsetiapp.common.model.response.EAPInsertResponse
import com.rsetiapp.common.model.response.EapAutoFetchRes
import com.rsetiapp.common.model.response.FollowUpStatus
import com.rsetiapp.common.model.response.CandidateDetailsRes
import com.rsetiapp.common.model.response.CandidateSearchResp
import com.rsetiapp.common.model.response.EapListResponse
import com.rsetiapp.common.model.response.FollowUpInsertRes
import com.rsetiapp.common.model.response.FollowUpStatusResp
import com.rsetiapp.common.model.response.FollowUpTypeResp
import com.rsetiapp.common.model.response.ForgotPassresponse
import com.rsetiapp.common.model.response.FormResponse
import com.rsetiapp.common.model.response.LoginRes
import com.rsetiapp.common.model.response.OtpGenerateResponse
import com.rsetiapp.common.model.response.ProgramResponse
import com.rsetiapp.common.model.response.SalaryRangeRes
import com.rsetiapp.core.data.local.database.AppDatabase
import com.rsetiapp.core.data.remote.AppLevelApi
import com.rsetiapp.core.di.AppModule
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.networkBoundResourceWithoutDb
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
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
            appLevelApi.getFollowUpBatchListAPI(BatchListReq(appVersion))
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

    suspend fun getCandidateAPI(
        appVersion: String, batchId: String
    ): Flow<Resource<out CandidateListResponse>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getFollowUpCandidateListAPI(CandidateListReq(appVersion, batchId))
        }
    }


    suspend fun getbankIFSCAPI(
       bankIFSCSearchReq: BankIFSCSearchReq
    ): Flow<Resource<out BankIFSCSearchRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.bankIFSCAPI(bankIFSCSearchReq)
        }
    }

    suspend fun getFollowTypeListAPI(appVersion: String): Flow<Resource<out FollowUpTypeResp>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getFollowTypeListAPI(FollowUpTypeReq( appVersion))
        }
    }

    suspend fun getFollowStatusListAPI(appVersion: String): Flow<Resource<out FollowUpStatusResp>>{
        return networkBoundResourceWithoutDb {
            appLevelApi.getFollowStatusListAPI(FollowUpTypeReq( appVersion))
        }
    }

    suspend fun getAttendanceBatchAPI(
        appVersion: String
    ): Flow<Resource<out AttendanceBatchRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getAttendanceBatch(AttendanceBatchReq(appVersion))
        }
    }

    suspend fun getAttendanceCandidate(
        appVersion: String, batchId:String
    ): Flow<Resource<out AttendanceCandidateRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getAttendanceCandidate(AttendanceCandidateReq(appVersion,batchId))
        }
    }

    suspend fun getAttendanceCheckStatus(
        attendanceCheckReq: AttendanceCheckReq
    ): Flow<Resource<out AttendanceCheckRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getAttendanceCheckStatus(attendanceCheckReq)
        }
    }

    suspend fun getInsertAttendance(
       attendanceInsertReq: AttendanceInsertReq
    ): Flow<Resource<out AttendanceInsertRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.getInsertAttendance(attendanceInsertReq)
        }
    }

    suspend fun insertFollowUpAPI(followUpInsertReq: FollowUpInsertReq): Flow<Resource<out FollowUpInsertRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.insertFollowUpAPI(followUpInsertReq)
        }
    }

    suspend fun postOnAUAFaceAuthNREGA(url:String, uidaiKycRequest: UidaiKycRequest): Flow<Resource<out Response<UidaiResp>>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.postOnAUAFaceAuthNREGA(url,uidaiKycRequest)
        }
    }

    suspend fun getSalaryDetailsAPI(salaryRangeReq: SalaryRangeReq): Flow<Resource<out SalaryRangeRes>> {
        return networkBoundResourceWithoutDb {
            appLevelApi.salaryRangeDetails(salaryRangeReq)
        }
    }




}