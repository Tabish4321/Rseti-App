package com.rsetiapp.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsetiapp.common.model.response.StateDataResponse
import com.rsetiapp.common.model.response.BlockResponse
import com.rsetiapp.common.model.response.DistrictResponse
import com.rsetiapp.common.model.response.VillageResponse
import com.rsetiapp.common.model.response.grampanchayatResponse
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.model.request.CandidateDetailsReq
import com.rsetiapp.common.model.request.CandidateSearchReq
import com.rsetiapp.common.model.request.EAPInsertRequest
import com.rsetiapp.common.model.request.EapListReq
import com.rsetiapp.common.model.request.FogotPaasReq
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.common.model.request.OtpGenerateRequest
import com.rsetiapp.common.model.response.Batch
import com.rsetiapp.common.model.response.BatchListResponse
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.common.model.response.CandidateListResponse
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
import com.rsetiapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CommonViewModel @Inject constructor(private val commonRepository: CommonRepository) :
    ViewModel() {


    private  var _getLoginAPI =  MutableStateFlow<Resource<out LoginRes>>(Resource.Loading())
    val getLoginAPI = _getLoginAPI.asStateFlow()


    fun getLoginAPI(loginReq: LoginReq) {
        viewModelScope.launch {
            commonRepository.getLoginAPI(loginReq).collectLatest {
                _getLoginAPI.emit(it)
            }
        }


    }

    private var _getFormAPI = MutableStateFlow<Resource<out FormResponse>>(Resource.Loading())
    val getFormAPI = _getFormAPI.asStateFlow()



    fun getFormAPI(appVersion:String , login:String){
        viewModelScope.launch {
            commonRepository.getFormAPI(appVersion,login).collectLatest {
                _getFormAPI.emit(it)
            }
        }


    }

    private var _stateList =  MutableStateFlow<Resource<out StateDataResponse>>(Resource.Loading())
    val getStateList = _stateList.asStateFlow()


    fun getStateListApi(){
        viewModelScope.launch {
            commonRepository.getStateListApi(BuildConfig.VERSION_NAME).collectLatest {
                _stateList.emit(it)
            }
        }
    }

    private var _districtList =  MutableStateFlow<Resource<out DistrictResponse>>(Resource.Loading())
    val getDistrictList = _districtList.asStateFlow()


    fun getDistrictListApi(state :String){
        viewModelScope.launch {
            commonRepository.getDistrictListApi(state,BuildConfig.VERSION_NAME).collectLatest {
                _districtList.emit(it)
            }
        }
    }


    private var _blockList =  MutableStateFlow<Resource<out BlockResponse>>(Resource.Loading())
    val getBlockList = _blockList.asStateFlow()


    fun getBlockListApi(district :String){
        viewModelScope.launch {
            commonRepository.getBlockListApi(district,BuildConfig.VERSION_NAME).collectLatest {
                _blockList.emit(it)
            }
        }}
    private  var _gpList =  MutableStateFlow<Resource<out grampanchayatResponse>>(Resource.Loading())
    val getGpList = _gpList.asStateFlow()


    fun getGpListApi(block :String) {
        viewModelScope.launch {
            commonRepository.getGPListApi(block, BuildConfig.VERSION_NAME).collectLatest {
                _gpList.emit(it)
            }
        }


    }

    private  var _villageList =  MutableStateFlow<Resource<out VillageResponse>>(Resource.Loading())
    val getVillageList = _villageList.asStateFlow()


    fun getVillageListApi(gp :String) {
        viewModelScope.launch {
            commonRepository.getVillageListApi(gp, BuildConfig.VERSION_NAME).collectLatest {
                _villageList.emit(it)
            }
        }


    }

    private  var _getProgramListAPI =  MutableStateFlow<Resource<out ProgramResponse>>(Resource.Loading())
    val getProgramListAPI = _getProgramListAPI.asStateFlow()


    fun getProgramListAPI() {
        viewModelScope.launch {
            commonRepository.getProgramListAPI( BuildConfig.VERSION_NAME).collectLatest {
                _getProgramListAPI.emit(it)
            }
        }


    }


    private  var _getEapAutoFetchListAPI =  MutableStateFlow<Resource<out EapAutoFetchRes>>(Resource.Loading())
    val getEapAutoFetchListAPI = _getEapAutoFetchListAPI.asStateFlow()


    fun getEapAutoFetchListAPI(login :String, appVersion: String) {
        viewModelScope.launch {
            commonRepository.getEapAutoFetchListAPI( login,  appVersion).collectLatest {
                _getEapAutoFetchListAPI.emit(it)
            }
        }


    }

    private  var _insertEAPAPI =  MutableStateFlow<Resource<out EAPInsertResponse>>(Resource.Loading())
    val insertEAPAPI = _insertEAPAPI.asStateFlow()


    fun insertEAPAPI(eapInsertRequest: EAPInsertRequest) {
        viewModelScope.launch {
            commonRepository.insertEAPAPI(eapInsertRequest).collectLatest {
                _insertEAPAPI.emit(it)
            }
        }


    }


    private  var _generateOtpAPI =  MutableStateFlow<Resource<out OtpGenerateResponse>>(Resource.Loading())
    val generateOtpAPI = _generateOtpAPI.asStateFlow()

    fun generateOtpAPI(otpGenerateRequest: OtpGenerateRequest) {
        viewModelScope.launch {
            commonRepository.generateOtpAPI(otpGenerateRequest).collectLatest {
                _generateOtpAPI.emit(it)
            }
        }


    }

    private  var _forgetPasswordAPI =  MutableStateFlow<Resource<out ForgotPassresponse>>(Resource.Loading())
    val forgetPasswordAPI = _forgetPasswordAPI.asStateFlow()

    fun forgetPasswordAPI(fogotPaasReq: FogotPaasReq) {
        viewModelScope.launch {
            commonRepository.forgetPasswordAPI(fogotPaasReq).collectLatest {
                _forgetPasswordAPI.emit(it)
            }
        }


    }

    private var _getBatchAPI = MutableStateFlow<Resource<out BatchListResponse>>(Resource.Loading())
    val getBatchAPI = _getBatchAPI.asStateFlow()

    fun getBatchAPI(appVersion:String , login:String){
        viewModelScope.launch {
            commonRepository.getBatchAPI(appVersion).collectLatest {
                _getBatchAPI.emit(it)
            }
        }
    }

    private var _getCandidateAPI = MutableStateFlow<Resource<out CandidateListResponse>>(Resource.Loading())
    val getCandidateAPI = _getCandidateAPI.asStateFlow()

    fun getCandidateAPI(appVersion:String , batchId: String){
        viewModelScope.launch {
            commonRepository.getCandidateAPI(appVersion, batchId).collectLatest {
                _getCandidateAPI.emit(it)
            }
        }
    }


    private  var _candidateSearchListAPI =  MutableStateFlow<Resource<out CandidateSearchResp>>(Resource.Loading())
    val candidateSearchListAPI = _candidateSearchListAPI.asStateFlow()

    fun candidateSearchListAPI(candidateSearchReq: CandidateSearchReq) {
        viewModelScope.launch {
            commonRepository.candidateSearchListAPI(candidateSearchReq).collectLatest {
                _candidateSearchListAPI.emit(it)
            }
        }


    }


    private  var _candidateDetailsAPI =  MutableStateFlow<Resource<out CandidateDetailsRes>>(Resource.Loading())
    val candidateDetailsAPI = _candidateDetailsAPI.asStateFlow()

    fun candidateDetailsAPI(candidateDetailsReq: CandidateDetailsReq) {
        viewModelScope.launch {
            commonRepository.candidateDetailsAPI(candidateDetailsReq).collectLatest {
                _candidateDetailsAPI.emit(it)
            }
        }


    }


    private  var _eapDetailsAPI =  MutableStateFlow<Resource<out EapListResponse>>(Resource.Loading())
    val eapDetailsAPI = _eapDetailsAPI.asStateFlow()

    fun eapDetailsAPI(eapListReq: EapListReq) {
        viewModelScope.launch {
            commonRepository.eapDetailsAPI(eapListReq).collectLatest {
                _eapDetailsAPI.emit(it)
            }
        }


    }


    private  var bankIfscCodeAPI =  MutableStateFlow<Resource<out BankDetailsResponse>>(Resource.Loading())
    val bankIfscCodeAPI = bankIfscCodeAPI.asStateFlow()

    fun bankIfscCodeAPI(eapListReq: EapListReq) {
        viewModelScope.launch {
            commonRepository.eapDetailsAPI(eapListReq).collectLatest {
                bankIfscCodeAPI.emit(it)
            }
        }


    }





}