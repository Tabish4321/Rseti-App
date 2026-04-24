package com.rsetiapp.common.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.SettledCandidateAdapter
import com.rsetiapp.common.adapter.SettlementBatchAdapter
import com.rsetiapp.common.adapter.SettlementVeryficationDetailsAdapter
import com.rsetiapp.common.model.request.SettlementPrefModel
import com.rsetiapp.common.model.response.CandidateSettlementVerificationDetail
import com.rsetiapp.common.model.response.GetSettledCandidateRes
import com.rsetiapp.common.model.response.SettledCandidate
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.databinding.FragmentSettlementVeryficationBatchCandidateBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class SettlementVeryficationBatchCandidateFragment :   BaseFragment<FragmentSettlementVeryficationBatchCandidateBinding>(FragmentSettlementVeryficationBatchCandidateBinding::inflate) {

    private var state = ""
    private var status = ""
    private var candidateId = ""
    private var batchName = ""


    private lateinit var settlementVeryfiationAdapter: SettlementVeryficationDetailsAdapter
    private lateinit var settledCandidateadapter: SettledCandidateAdapter

//    @Inject
//    lateinit var userPreferences: UserPreferences

    private val candidateSettledList = mutableListOf<SettledCandidate>()

    private val SettlementVeryficationBatch = mutableListOf<CandidateSettlementVerificationDetail>()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        init()
        setupAdapter()
    }

    override fun onResume() {
        super.onResume()

        collectCandidatesData()
    }

    private fun init() {
        binding.tvTitleName.text = getString(R.string.settlement_veryfication_form)
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

//    private fun setupRecyclerView() {
//        settlementVeryfiationAdapter = SettlementVeryficationDetailsAdapter(SettlementVeryficationBatch) { candidate ->
//
//            val model = SettlementPrefModel(
//                instituteId = AppUtil.getSavedinstituteIdPreference(requireContext()) ?: "",
//                candidateId = candidate.candidateId ?: "",
//                candidateName = candidate.candidateName ?: "",
//                mobileNo = candidate.mobileNo ?: "",
//                guardianName = candidate.guardianName ?: "",
//                guardianMobileNo = candidate.guardianMobileNo ?: "",
//                aadharBlockName = candidate.aadharBlockName ?: "",
//                aadharPinCode = candidate.aadharPinCode ?: "",
//                settlementId = candidate.settlementId?.toString() ?: "",
//                followUpId = candidate.followUpId?.toString() ?: "",
//                batchId = candidate.batchId?.toString() ?: "",
//                ifscCode = candidate.ifscCode ?: "",
//                loanAccountNo = candidate.loanAccountNo ?: "",
//                creditFromBank = candidate.creditFromBank ?: "",
//                selfInvestment = candidate.selfInvestment ?: "",
//                totalInvestment = candidate.totalInvestment ?: "",
//                passbookCopy = candidate.passbookCopy ?: "",
//                appointmentLetter = candidate.appointmentLetter ?: "",
//                settlementPhoto = candidate.settlementPhoto ?: "",
//                updatedBy = candidate.updatedBy ?: "",
//                latitude = candidate.latitude ?: "0.0",
//                longitude = candidate.longitude ?: "0.0",
//                rollNo = candidate.rollNo?.toString() ?: "",
//                cityName = candidate.cityName ?: "",
//                settlementReason = candidate.settlementReason ?: "",
//                accountStatus = candidate.accountStatus ?: "",
//                salaryRange = candidate.salaryRange ?: "",
//                employmentGiven = candidate.employmentGiven ?: "",
//                familyMemberPartTime = candidate.familyMemberPartTime ?: "",
//                bankName = candidate.bankName ?: "",
//                branchName = candidate.branchName ?: "",
//                followupType = candidate.followupType ?: "",
//                statusName = candidate.statusName ?: "",
//                salaryRangeId = candidate.salaryRangeId ?: ""
//            )
//
//
//            val gson = GsonBuilder().setPrettyPrinting().create()
//            val requestJson = gson.toJson(model)
//
//            Log.d("ReverificationAPI", "📤 REQUEST JSON:\n$requestJson")
//            AppUtil.saveItem(requireContext(), model)
//
//
//
//            val bottomSheet = VeryficationSattelementBottomSheet()
//            bottomSheet.show(parentFragmentManager, "MySattelementBottomSheet")
//
//        }
//
//
//        binding.rvCandidate.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvCandidate.adapter = settledCandidateadapter
//    }

    private fun setupAdapter() {

        settledCandidateadapter = SettledCandidateAdapter(
            candidateSettledList
        ) { candidate ->

            // 🔹 Item Click Toast
            Toast.makeText(
                requireContext(),
                "Clicked: ${candidate.candidateName}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvCandidate.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCandidate.adapter = settledCandidateadapter
    }

//    private fun setupRecyclerView() {
//        settledCandidateadapter = SettledCandidateAdapter(candidateSettledList) { candidate ->
//
//            val model = SettledCandidate(
//
//                candidateId = candidate.candidateId ?: "",
//                candidateName = candidate.candidateName ?: "",
//                mobileNo = candidate.mobileNo ?: "",
//                guardianMobileNo = candidate.guardianMobileNo ?: "",
//                rollNo = candidate.rollNo?.toString()
//            )
//
//
//            val gson = GsonBuilder().setPrettyPrinting().create()
//            val requestJson = gson.toJson(model)
//
//            Log.d("ReverificationAPI", "📤 REQUEST JSON:\n$requestJson")
//            AppUtil.saveItem(requireContext(), model)
//
//
//
//            val bottomSheet = VeryficationSattelementBottomSheet()
//            bottomSheet.show(parentFragmentManager, "MySattelementBottomSheet")
//
//        }
//
//
//        binding.rvCandidate.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvCandidate.adapter = settledCandidateadapter
//    }


    @SuppressLint("NotifyDataSetChanged")


//     private var _SettledCandidate =
//        MutableStateFlow<Resource<out GetSettledCandidate>>(Resource.Loading())
//    val SettledCandidate = _reverificationSettlement.asStateFlow()
//
//    fun getSettledCandidateAPI(getsettledcandidateReq: GetSettledCandidateReq) {
//        viewModelScope.launch {
//            commonRepository.getSettledCandidateAPI(getsettledcandidateReq).collectLatest {
//                _SettledCandidate.emit(it)
//            }
//
//
//        }}


    private fun collectCandidatesData() {

//        commonViewModel.getSettledCandidateAPI(
//            AppUtil.getSavedTokenPreference(requireContext()),
//            "",
//            "",
//            "",
//            0
//
//            )

        commonViewModel.getSettledCandidateAPI(
            AppUtil.getSavedTokenPreference(requireContext()),
            userPreferences.getUseID(),
            BuildConfig.VERSION_NAME,
            AppUtil.getAndroidId(requireContext()),
            AppUtil.getSavedbatchIdPreference(requireContext()).toInt()

        )

//        ,
//            BuildConfig.VERSION_NAME,
//            batchId,AppUtil.getAndroidId(requireContext()),userPreferences.getUseID()


//        commonViewModel.getSettledCandidateAPI(GetSettledCandidateReq(
//            "TSRLM",
//            BuildConfig.VERSION_NAME,
//            "b428b8c6b12da585",
//            17
//        )
//        )


//              "login":"TSRLM",
//    "appVersion":"1.1",
//    "imeiNo":"b428b8c6b12da585",
//    "batchId":17
//            GetSettledCandidateReq(
//                AppUtil.getSavedCandidatePreference(requireContext()),
//                BuildConfig.VERSION_NAME,
//                "",
//                userPreferences.getUseID()
//            )
//        )


        lifecycleScope.launch {
            commonViewModel.SettledCandidate.collectLatest { resource ->
                when (resource) {

                    is Resource.Loading -> showProgressBar()

                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(resource.error?.message ?: "Internal Server Error")
                        candidateSettledList.clear()
                        binding.rvCandidate.adapter = null

                    }

                    is Resource.Success -> {
                        hideProgressBar()


                        val list = resource.data?.wrappedList
                        if (!list.isNullOrEmpty()) {
                            setupAdapter()
                            candidateSettledList.clear()
                            candidateSettledList.addAll(list)
                        } else {
                            candidateSettledList.clear()
                            binding.rvCandidate.adapter = null
                            showSnackBar("No settlement data available")
                        }
                    }
                }
            }
        }


    }


}


































//    Please donnot delete code


//        lifecycleScope.launch {
//            commonViewModel.getsettlementVeryfication.collectLatest { resource ->
//                when (resource) {
//
//                    is Resource.Loading -> showProgressBar()
//
//                    is Resource.Error -> {
//                        hideProgressBar()
//                        showSnackBar(resource.error?.message ?: "Internal Server Error")
//                    }
//
//                    is Resource.Success -> {
//                        hideProgressBar()
//
//                        val list = resource.data?.wrappedList
//                        if (!list.isNullOrEmpty()) {
//                            SettlementVeryficationBatch.clear()
//                            SettlementVeryficationBatch.addAll(list)
//                            settlementVeryfiationAdapter.notifyDataSetChanged()
//                        } else {
//                            showSnackBar("No settlement data available")
//                        }
//                    }
//                }
//            }
//        }

