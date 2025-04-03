package com.rsetiapp.core.data.remote

import com.rsetiapp.common.model.request.AttendanceBatchReq
import com.rsetiapp.common.model.request.AttendanceCandidateReq
import com.rsetiapp.common.model.request.AttendanceCheckReq
import com.rsetiapp.common.model.request.AttendanceInsertReq
import com.rsetiapp.common.model.request.BatchListReq
import com.rsetiapp.common.model.request.BlockReq
import com.rsetiapp.common.model.request.CandidateDetailsReq
import com.rsetiapp.common.model.request.CandidateListReq
import com.rsetiapp.common.model.request.CandidateSearchReq
import com.rsetiapp.common.model.request.DistrictReq
import com.rsetiapp.common.model.request.EAPInsertRequest
import com.rsetiapp.common.model.request.EapAutofetchReq
import com.rsetiapp.common.model.request.EapListReq
import com.rsetiapp.common.model.request.FogotPaasReq
import com.rsetiapp.common.model.request.FollowUpInsertReq
import com.rsetiapp.common.model.request.FollowUpTypeReq
import com.rsetiapp.common.model.request.FormRequest
import com.rsetiapp.common.model.request.GramPanchayatReq
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.common.model.request.OtpGenerateRequest
import com.rsetiapp.common.model.request.StateListReq
import com.rsetiapp.common.model.request.VillageReq
import com.rsetiapp.common.model.response.AttendanceBatchRes
import com.rsetiapp.common.model.response.AttendanceCandidateRes
import com.rsetiapp.common.model.response.AttendanceCheckRes
import com.rsetiapp.common.model.response.AttendanceInsertRes
import com.rsetiapp.common.model.response.BatchListResponse
import com.rsetiapp.common.model.response.BlockResponse
import com.rsetiapp.common.model.response.CandidateDetailsRes
import com.rsetiapp.common.model.response.CandidateListResponse
import com.rsetiapp.common.model.response.CandidateSearchResp
import com.rsetiapp.common.model.response.DistrictResponse
import com.rsetiapp.common.model.response.EAPInsertResponse
import com.rsetiapp.common.model.response.EapAutoFetchRes
import com.rsetiapp.common.model.response.EapListResponse
import com.rsetiapp.common.model.response.FollowUpInsertRes
import com.rsetiapp.common.model.response.FollowUpStatusResp
import com.rsetiapp.common.model.response.FollowUpTypeResp
import com.rsetiapp.common.model.response.ForgotPassresponse
import com.rsetiapp.common.model.response.FormResponse
import com.rsetiapp.common.model.response.LoginRes
import com.rsetiapp.common.model.response.OtpGenerateResponse
import com.rsetiapp.common.model.response.ProgramResponse
import com.rsetiapp.common.model.response.StateDataResponse
import com.rsetiapp.common.model.response.VillageResponse
import com.rsetiapp.common.model.response.grampanchayatResponse
import com.rsetiapp.core.uidai.ekyc.UidaiKycRequest
import com.rsetiapp.core.uidai.ekyc.UidaiResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AppLevelApi {


    @POST("login")
    suspend fun getLoginAPI(@Body loginReq: LoginReq): LoginRes

    @POST("forms")
    suspend fun getFormAPI(@Body formRequest: FormRequest): FormResponse


    @POST("stateList")
    suspend fun getStateListAPI(@Body stateListReq: StateListReq): StateDataResponse

    @POST("districtList")
    suspend fun getDistrictListAPI(@Body districtReq: DistrictReq): DistrictResponse

    @POST("blockList")
    suspend fun getBlockListAPI(@Body blockReq: BlockReq): BlockResponse


    @POST("gramPanchayatList")
    suspend fun getGpListAPI(@Body gramPanchayatReq: GramPanchayatReq): grampanchayatResponse


    @POST("villageList")
    suspend fun getVillageListAPI(@Body villageReq: VillageReq): VillageResponse


    @POST("program")
    suspend fun getProgramListAPI(@Body stateListReq: StateListReq): ProgramResponse


    @POST("eapautofetch")
    suspend fun getEapAutoFetchListAPI(@Body eapAutofetchReq: EapAutofetchReq): EapAutoFetchRes

    @POST("insertEap")
    suspend fun insertEAPAPI(@Body eapInsertRequest: EAPInsertRequest): EAPInsertResponse

    @POST("verifiyMobile")
    suspend fun generateOtpAPI(@Body otpGenerateRequest: OtpGenerateRequest): OtpGenerateResponse

    @POST("forgetPassword")
    suspend fun forgetPasswordAPI(@Body fogotPaasReq: FogotPaasReq): ForgotPassresponse

    @POST("batchList")
    suspend fun getFollowUpBatchListAPI(@Body batchListReq: BatchListReq): BatchListResponse

    @POST("batchCandidateList")
    suspend fun getFollowUpCandidateListAPI(@Body candidateListReq: CandidateListReq): CandidateListResponse

    @POST("candidateList")
    suspend fun candidateSearchListAPI(@Body candidateSearchReq: CandidateSearchReq): CandidateSearchResp


    @POST("candidateDetails")
    suspend fun candidateDetailsAPI(@Body candidateDetailsReq: CandidateDetailsReq): CandidateDetailsRes

    @POST("eapDetails")
    suspend fun eapDetailsAPI(@Body eapListReq: EapListReq): EapListResponse

    @POST("followUpType")
    suspend fun getFollowTypeListAPI(@Body followUpTypeReq: FollowUpTypeReq): FollowUpTypeResp

    @POST("followUpStatus")
    suspend fun getFollowStatusListAPI(@Body followUpTypeReq: FollowUpTypeReq): FollowUpStatusResp

    @POST("onGoingBatchList")
    suspend fun getAttendanceBatch(@Body attendanceBatchReq: AttendanceBatchReq): AttendanceBatchRes

    @POST("onGoingBatchCandidateList")
    suspend fun getAttendanceCandidate(@Body attendanceCandidateReq: AttendanceCandidateReq): AttendanceCandidateRes

    @POST("insertFollowUp")
    suspend fun insertFollowUpAPI(@Body followUpInsertReq: FollowUpInsertReq): FollowUpInsertRes

    @POST("attandanceCheck")
    suspend fun getAttendanceCheckStatus( @Body attendanceCheckReq: AttendanceCheckReq): AttendanceCheckRes


    @POST("insertAttandance")
    suspend fun getInsertAttendance( @Body attendanceInsertReq: AttendanceInsertReq): AttendanceInsertRes
    @POST
    suspend fun postOnAUAFaceAuthNREGA(
        @Url url: String, @Body request: UidaiKycRequest
    ): Response<UidaiResp>
}