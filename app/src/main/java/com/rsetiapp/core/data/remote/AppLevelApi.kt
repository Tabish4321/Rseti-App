package com.rsetiapp.core.data.remote

import com.rsetiapp.common.model.request.AttendanceBatchReq
import com.rsetiapp.common.model.request.AttendanceCandidateReq
import com.rsetiapp.common.model.request.AttendanceCheckReq
import com.rsetiapp.common.model.request.AttendanceInsertReq
import com.rsetiapp.common.model.request.BankIFSCSearchReq
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
import com.rsetiapp.common.model.request.InsertSdrVisitReq
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.common.model.request.OtpGenerateRequest
import com.rsetiapp.common.model.request.SalaryRangeReq
import com.rsetiapp.common.model.request.SdrListReq
import com.rsetiapp.common.model.request.SettleStatusRequest
import com.rsetiapp.common.model.request.StateListReq
import com.rsetiapp.common.model.request.TokenReq
import com.rsetiapp.common.model.request.ValidateOtpReq
import com.rsetiapp.common.model.request.VillageReq
import com.rsetiapp.common.model.response.BankIFSCSearchRes
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
import com.rsetiapp.common.model.response.SalaryRangeRes
import com.rsetiapp.common.model.response.SdrInsertResp
import com.rsetiapp.common.model.response.SdrListResp
import com.rsetiapp.common.model.response.SettleStatusResponse
import com.rsetiapp.common.model.response.StateDataResponse
import com.rsetiapp.common.model.response.TokenRes
import com.rsetiapp.common.model.response.VillageResponse
import com.rsetiapp.common.model.response.grampanchayatResponse
import com.rsetiapp.core.uidai.ekyc.UidaiKycRequest
import com.rsetiapp.core.uidai.ekyc.UidaiResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface AppLevelApi {

    @POST("generateToken")
    suspend fun getToken(@Body tokenReq: TokenReq): TokenRes

    @POST("login")
    suspend fun getLoginAPI(@Body loginReq: LoginReq): LoginRes

    @POST("forms")
    suspend fun getFormAPI(@Header("Authorization") token: String,
                           @Body formRequest: FormRequest): FormResponse


    @POST("stateList")
    suspend fun getStateListAPI(@Header("Authorization") token: String,
                                @Body stateListReq: StateListReq): StateDataResponse

    @POST("districtList")
    suspend fun getDistrictListAPI(@Header("Authorization") token: String,
                                   @Body districtReq: DistrictReq): DistrictResponse

    @POST("blockList")
    suspend fun getBlockListAPI(@Header("Authorization") token: String,
                                @Body blockReq: BlockReq): BlockResponse


    @POST("gramPanchayatList")
    suspend fun getGpListAPI(@Header("Authorization") token: String,
                             @Body gramPanchayatReq: GramPanchayatReq): grampanchayatResponse


    @POST("villageList")
    suspend fun getVillageListAPI(@Header("Authorization") token: String,
                                  @Body villageReq: VillageReq): VillageResponse


    @POST("program")
    suspend fun getProgramListAPI(@Header("Authorization") token: String,
                                  @Body stateListReq: StateListReq): ProgramResponse


    @POST("eapautofetch")
    suspend fun getEapAutoFetchListAPI(@Header("Authorization") token: String,
                                       @Body eapAutofetchReq: EapAutofetchReq): EapAutoFetchRes

    @POST("insertEap")
    suspend fun insertEAPAPI(@Header("Authorization") token: String,
                             @Body eapInsertRequest: EAPInsertRequest): EAPInsertResponse

    @POST("verifiyMobile")
    suspend fun generateOtpAPI(@Body otpGenerateRequest: OtpGenerateRequest): OtpGenerateResponse

    @POST("forgetPassword")
    suspend fun forgetPasswordAPI(@Body fogotPaasReq: FogotPaasReq): ForgotPassresponse

    @POST("batchList")
    suspend fun getFollowUpBatchListAPI(@Header("Authorization") token : String,
                                        @Body batchListReq: BatchListReq): BatchListResponse

    @POST("batchCandidateList")
    suspend fun getFollowUpCandidateListAPI(@Header("Authorization") token: String,
                                            @Body candidateListReq: CandidateListReq): CandidateListResponse

    @POST("candidateList")
    suspend fun candidateSearchListAPI(@Header("Authorization") token: String,
                                       @Body candidateSearchReq: CandidateSearchReq): CandidateSearchResp


    @POST("candidateDetails")
    suspend fun candidateDetailsAPI(@Header("Authorization") token: String,
                                    @Body candidateDetailsReq: CandidateDetailsReq): CandidateDetailsRes

    @POST("eapDetails")
    suspend fun eapDetailsAPI(@Header("Authorization") token: String,
                              @Body eapListReq: EapListReq): EapListResponse

    @POST("followUpType")
    suspend fun getFollowTypeListAPI(@Header("Authorization") token: String,
                                     @Body followUpTypeReq: FollowUpTypeReq): FollowUpTypeResp

    @POST("followUpStatus")
    suspend fun getFollowStatusListAPI(@Header("Authorization") token: String,
                                       @Body followUpTypeReq: FollowUpTypeReq): FollowUpStatusResp



    @POST("bankDetailsByIfsc")
    suspend fun bankIFSCAPI(@Header("Authorization") token: String,
                            @Body bankIFSCSearchReq: BankIFSCSearchReq): BankIFSCSearchRes


    @POST("onGoingBatchList")
    suspend fun getAttendanceBatch(@Header("Authorization") token: String,
                                   @Body attendanceBatchReq: AttendanceBatchReq): AttendanceBatchRes

    @POST("onGoingBatchCandidateList")
    suspend fun getAttendanceCandidate(@Header("Authorization") token: String,
                                       @Body attendanceCandidateReq: AttendanceCandidateReq): AttendanceCandidateRes

    @POST("insertFollowUp")
    suspend fun insertFollowUpAPI(@Header("Authorization") token: String,
                                  @Body followUpInsertReq: FollowUpInsertReq): FollowUpInsertRes

    @POST("attandanceCheck")
    suspend fun getAttendanceCheckStatus(@Header("Authorization") token: String,
                                         @Body attendanceCheckReq: AttendanceCheckReq): AttendanceCheckRes


    @POST("insertAttandance")
    suspend fun getInsertAttendance( @Header("Authorization") token: String,
                                     @Body attendanceInsertReq: AttendanceInsertReq): AttendanceInsertRes
    @POST("salaryRange")
    suspend fun salaryRangeDetails(@Header("Authorization") token: String,
                                   @Body salaryRangeReq: SalaryRangeReq) : SalaryRangeRes

    @POST("insertsdr")
    suspend fun insertSdrApi(@Header("Authorization") token: String,
                                   @Body sdrVisitReq: InsertSdrVisitReq) : SdrInsertResp


    @POST("sdrList")
    suspend fun sdrListApi(@Header("Authorization") token: String,
                                   @Body sdrListReq: SdrListReq) : SdrListResp


    @POST("validateOtp")
    suspend fun getOtpValidateApi(@Body validateOtpReq: ValidateOtpReq):OtpGenerateResponse
    @POST
    suspend fun postOnAUAFaceAuthNREGA(
        @Url url: String, @Body request: UidaiKycRequest
    ): Response<UidaiResp>


    @POST("candidateSettleStatus")
    suspend fun getSettleStatusApi(@Body  settleStatusRequest: SettleStatusRequest ) : SettleStatusResponse


}