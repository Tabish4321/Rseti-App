package com.rsetiapp.common.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.MySattelementBottomSheet
import com.rsetiapp.common.adapter.SettlementBatchAdapter
import com.rsetiapp.common.model.request.DistrictListReq
import com.rsetiapp.common.model.request.InstituteListReq
import com.rsetiapp.common.model.request.SettlementVeryficationBatchReq
import com.rsetiapp.common.model.response.DistrictList
import com.rsetiapp.common.model.response.Institutes
import com.rsetiapp.common.model.response.SettlementPercentage
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.AppUtil.convertMonthNumberToFullName
import com.rsetiapp.core.util.AppUtil.getCurrentYear
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.databinding.FragmentSettlementVeryficationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


//@AndroidEntryPoint
//class SettlementVeryficationFragment :
//    BaseFragment<FragmentSettlementVeryficationBinding>(
//        FragmentSettlementVeryficationBinding::inflate
//    ) {
//
//    private lateinit var districtAdapter: ArrayAdapter<String>
//    private lateinit var instituteAdapter: ArrayAdapter<String>
//
//    private var districtList: List<DistrictList> = emptyList()
//    private var instituteList: List<Institutes> = emptyList()
//
//    private val districtNames = ArrayList<String>()
//    private val instituteNames = ArrayList<String>()
//
//    private val commonViewModel: CommonViewModel by activityViewModels()
//
//    private lateinit var batchAdapter: SettlementBatchAdapter
//    private val batchList = mutableListOf<SettlementPercentage>()
//
//    private var instituteJob: Job? = null
//    private var batchJob: Job? = null
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        init()
//    }
//
//    private fun init() {
//
//        binding.tvTitleName.text = getString(R.string.settlement_batch)
//
//        binding.backButton.setOnClickListener {
//            findNavController().navigateUp()
//        }
//
//        setupRecycler()
//        setupDistrictSpinner()
//        setupInstituteSpinner()
//
//        // 🔥 Load district
//        if (commonViewModel.districtCache.isNotEmpty()) {
//            setDistrictData(commonViewModel.districtCache)
//        } else {
//            val value = AppUtil.getSavedEntityPreference(requireContext())
//            val lastTwoDigits = value.takeLast(2)
//
//            commonViewModel.getdistrictListAPI(
//                DistrictListReq(lastTwoDigits, BuildConfig.VERSION_NAME)
//            )
//            observeDistrict()
//        }
//    }
//
//    // ---------------- Recycler ----------------
//
//    private fun setupRecycler() {
//        batchAdapter = SettlementBatchAdapter(batchList) {
//            commonViewModel.selectedBatchPosition = it
//        }
//
//        binding.rvBatch.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvBatch.adapter = batchAdapter
//    }
//
//    // ---------------- District ----------------
//
//    private fun setupDistrictSpinner() {
//
//        districtAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            districtNames
//        )
//
//        binding.spinnerDistrict.setAdapter(districtAdapter)
//
//        binding.spinnerDistrict.setOnItemClickListener { _, _, position, _ ->
//
//            val selected = districtList[position]
//
//            commonViewModel.selectedDistrictId = selected.districtCode
//
//            // 🔥 FIX: clear cache
//            commonViewModel.instituteCache = emptyList()
//            commonViewModel.batchCache = emptyList()
//            commonViewModel.selectedInstituteId = null
//
//            instituteNames.clear()
//            instituteAdapter.notifyDataSetChanged()
//
//            batchList.clear()
//            batchAdapter.notifyDataSetChanged()
//
//            loadInstitute(selected.districtCode)
//        }
//    }
//
//    private fun observeDistrict() {
//        lifecycleScope.launch {
//            commonViewModel.districtList.collectLatest { resource ->
//                if (resource is Resource.Success) {
//                    val list = resource.data?.districtList ?: return@collectLatest
//                    commonViewModel.districtCache = list
//                    setDistrictData(list)
//                }
//            }
//        }
//    }
//
//    private fun setDistrictData(list: List<DistrictList>) {
//
//        districtList = list
//        districtNames.clear()
//        list.forEach { districtNames.add(it.districtName) }
//
//        districtAdapter.notifyDataSetChanged()
//
//        val selectedId = commonViewModel.selectedDistrictId
//        val index = list.indexOfFirst { it.districtCode == selectedId }
//
//        if (index != -1) {
//            binding.spinnerDistrict.setText(districtNames[index], false)
//            loadInstitute(list[index].districtCode)
//        }
//    }
//
//    // ---------------- Institute ----------------
//
//    private fun setupInstituteSpinner() {
//
//        instituteAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            instituteNames
//        )
//
//        binding.spinnerInstitute.setAdapter(instituteAdapter)
//
//        binding.spinnerInstitute.setOnItemClickListener { _, _, position, _ ->
//
//            val selected = instituteList[position]
//
//            commonViewModel.selectedInstituteId = selected.instituteId
//
//            // 🔥 clear old batch cache
//            commonViewModel.batchCache = emptyList()
//
//            loadBatch(selected.instituteId)
//        }
//    }
//
//    private fun loadInstitute(districtCode: String) {
//
//        // 🔥 ALWAYS CALL API (fix)
//        commonViewModel.instituteListAPI(
//            AppUtil.getSavedTokenPreference(requireContext()),
//
//            InstituteListReq(
//                appVersion = BuildConfig.VERSION_NAME,
//        login = userPreferences.getUseID(),
//        imeiNo = AppUtil.getAndroidId(requireContext()),
//        districtCode = districtCode
////                districtCode = "0551"
//        )
//
//
//
//
////            InstituteListReq(
////                BuildConfig.VERSION_NAME,
////                "",
////                AppUtil.getAndroidId(requireContext()),
////                districtCode
////            )
//        )
//
//        observeInstitute()
//    }
//
//    private fun observeInstitute() {
//
//        instituteJob?.cancel()
//
//        instituteJob = lifecycleScope.launch {
//            commonViewModel.instituteListAPI.collectLatest { resource ->
//
//                when (resource) {
//
//                    is Resource.Success -> {
//                        val list = resource.data?.wrappedList ?: emptyList()
//
//                        commonViewModel.instituteCache = list
//                        setInstituteData(list)
//                    }
//
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), "Institute error", Toast.LENGTH_SHORT).show()
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }
//
//    private fun setInstituteData(list: List<Institutes>) {
//
//        instituteList = list
//        instituteNames.clear()
//        list.forEach { instituteNames.add(it.instituteName) }
//
//        instituteAdapter.notifyDataSetChanged()
//
//        val selectedId = commonViewModel.selectedInstituteId
//        val index = list.indexOfFirst { it.instituteId == selectedId }
//
//        if (index != -1) {
//            binding.spinnerInstitute.setText(instituteNames[index], false)
//            loadBatch(list[index].instituteId)
//        }
//    }
//
//    // ---------------- Batch ----------------
//
//    private fun loadBatch(instituteId: String) {
//
//        commonViewModel.getsettledbatchAPI(
//            SettlementVeryficationBatchReq(
//                BuildConfig.VERSION_NAME,
//                instituteId
//            )
//        )
//
//        observeBatch()
//    }
//
//    private fun observeBatch() {
//
//        batchJob?.cancel()
//
//        batchJob = lifecycleScope.launch {
//            commonViewModel.getsettledbatchAPI.collectLatest { resource ->
//
//                when (resource) {
//
//                    is Resource.Success -> {
//                        val list = resource.data?.wrappedList ?: emptyList()
//
//                        commonViewModel.batchCache = list
//                        setBatchData(list)
//                    }
//
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), "Batch error", Toast.LENGTH_SHORT).show()
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }
//
//    private fun setBatchData(list: List<SettlementPercentage>) {
//
//        batchList.clear()
//        batchList.addAll(list)
//
//        batchAdapter.notifyDataSetChanged()
//
//        // 🔴 restore selected
//        batchAdapter.selectedPosition = commonViewModel.selectedBatchPosition
//        batchAdapter.notifyDataSetChanged()
//    }
//}








@AndroidEntryPoint
class SettlementVeryficationFragment : BaseFragment<FragmentSettlementVeryficationBinding>(FragmentSettlementVeryficationBinding::inflate) {

    private var formName = ""
    private lateinit var DistrictAdapter: ArrayAdapter<String>
    private lateinit var InstituteAdapter: ArrayAdapter<String>
    private var selectedDistrictIdValue: String? = null
    private var selectedInstituteIdValue: String? = null
    private var DistrictList: List<DistrictList> = mutableListOf()
    private var InstituteList: List<Institutes> = mutableListOf()
    private var DistrictName = ArrayList<String>()
    private var InstituteName = ArrayList<String>()
    private val commonViewModel: CommonViewModel by activityViewModels()


    private var years = arrayListOf<String>()
    private var months = arrayListOf<String>()
    private lateinit var settlementVeryfiationAdapter: SettlementBatchAdapter
    private val batchList = mutableListOf<SettlementPercentage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())
        formName = arguments?.getString("formName").toString()

        init()

//        if (batchFilteredList.isEmpty()) {
//            NoDataHelper.showNoData(binding.container, title = "No Data Found")
//        } else {
//            NoDataHelper.hideNoData(binding.container)
//        }
    }

    private fun init() {
//        binding.tvTitleName.text = formName
        binding.tvTitleName.text = getString(R.string.settlement_batch)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        years =
            (arrayListOf("All") + (2024..getCurrentYear()).map { it.toString() }) as ArrayList<String>
        months =
            (arrayListOf("All") + (1..12).map { convertMonthNumberToFullName(it) }) as ArrayList<String>
//        commonViewModel.getFollowTypeListAPI(AppUtil.getSavedTokenPreference(requireContext()),AppUtil.getAndroidId(requireContext()),userPreferences.getUseID())
//        commonViewModel.getdistrictListAPI(AppUtil.getSavedTokenPreference(requireContext()),AppUtil.getAndroidId(requireContext()),userPreferences.getUseID())
        val value = AppUtil.getSavedEntityPreference(requireContext())
        val lastTwoDigits = value.takeLast(2)
        commonViewModel.getdistrictListAPI(DistrictListReq(lastTwoDigits, BuildConfig.VERSION_NAME))
        collectFollowTypeResponse()

//        setupFilters()

        DistrictAdapter()

        Institutelistner()
        setupAdapter()


    }


    private fun setupAdapter() {
        settlementVeryfiationAdapter = SettlementBatchAdapter(batchList)
        binding.rvBatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBatch.adapter = settlementVeryfiationAdapter






    }

    private fun Institutelistner() {
        //Adapter Follow Up Type
        InstituteAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, InstituteName
        )
        binding.spinnerInstitute.setAdapter(InstituteAdapter)
        binding.spinnerInstitute.setOnItemClickListener { parent, view, position, id ->
            val selected = InstituteList[position]
            selectedInstituteIdValue = selected.instituteId
            commonViewModel.getsettledbatchAPI(SettlementVeryficationBatchReq(BuildConfig.VERSION_NAME,selected.instituteId))
            collectBatchesData()
            AppUtil.saveinstituteIdPreference(requireContext(),selected.instituteId)
        }
    }
    private fun DistrictAdapter() {
        //Adapter Follow Up Type


        DistrictAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, DistrictName
        )
        binding.spinnerDistrict.setAdapter(DistrictAdapter)
        binding.spinnerDistrict.setOnItemClickListener { parent, view, position, id ->
            val selected = DistrictList[position]
            selectedDistrictIdValue = selected.districtCode
            // Clear institute data
            InstituteAdapter.clear()
            InstituteName.clear()
            binding.spinnerInstitute.setText("", false)

            // Clear batch data
            batchList.clear()
            binding.rvBatch.adapter = null

//            settlementVeryfiationAdapter.update(emptyList()) // IMPORTANT

//            InstituteAdapter.clear()
//            InstituteName.clear()
            binding.spinnerInstitute.text
            val request = InstituteListReq(
                appVersion = BuildConfig.VERSION_NAME,
                login = userPreferences.getUseID(),
                imeiNo = AppUtil.getAndroidId(requireContext()),
                districtCode = selected.districtCode
//                districtCode = "0551"
            )



            commonViewModel.instituteListAPI(
                token = AppUtil.getSavedTokenPreference(requireContext()).removePrefix("Bearer ").trim(),
                request = request
            )

            InstituteListResponse()


            //Adapter Follow Up Type

        }

    }

//    fun update(newList: List<SettlementPercentage>) {
//        batchList.clear()
//        batchList.addAll(newList)
////        notifyDataSetChanged()
//    }
    private fun InstituteListResponse() {

        lifecycleScope.launch {
            commonViewModel.instituteListAPI.collectLatest { resource ->

                when (resource) {

                    is Resource.Loading -> {
                        showProgressBar()
                    }

                    is Resource.Error -> {
                        hideProgressBar()

                        Toast.makeText(
                            requireContext(),
                            resource.error?.message ?: "Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Success -> {
                        hideProgressBar()

                        val list = resource.data?.wrappedList

                        if (!list.isNullOrEmpty()) {

                            // 1️⃣ Assign full list
                            InstituteList = list

                            // 2️⃣ Populate district names
                            InstituteName.clear()
                            for (item in list) {
                                InstituteName.add(item.instituteName)
                            }

                            // 3️⃣ Notify adapter
                            InstituteAdapter.notifyDataSetChanged()

                        } else {
                            showSnackBar("No district data available")
                        }
                    }
                }
            }
        }
    }

    private fun collectFollowTypeResponse() {
        lifecycleScope.launch {
            commonViewModel.districtList.collectLatest { resource ->

                when (resource) {

                    is Resource.Loading -> {
                        showProgressBar()
                    }

                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(resource.error?.message ?: "Internal Server Error")
                    }

                    is Resource.Success -> {
                        hideProgressBar()

                        val list = resource.data?.districtList

                        if (!list.isNullOrEmpty()) {

                            // 1️⃣ Set adapter data
                            DistrictList = list

                            // 2️⃣ Populate the name list
                            DistrictName.clear()
                            for (x in list) {
                                DistrictName.add(x.districtName)
                            }

                            // 3️⃣ Notify adapter
                            DistrictAdapter.notifyDataSetChanged()

                        } else {
                            showSnackBar("No district data available")
                        }
                    }
                }
            }
        }












    }



    private fun collectBatchesData() {


        lifecycleScope.launch {
            commonViewModel.getsettledbatchAPI.collectLatest { resource ->
                when (resource) {

                    is Resource.Loading -> showProgressBar()

                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(resource.error?.message ?: "Internal Server Error")
                        batchList.clear()
                        binding.rvBatch.adapter = null

                    }

                    is Resource.Success -> {
                        hideProgressBar()


                        val list = resource.data?.wrappedList
                        if (!list.isNullOrEmpty()) {
//                            batchList.clear()
                            batchList.addAll(list)
                            setupAdapter()
//                            settlementVeryfiationAdapter.notifyDataSetChanged()
                        } else {
                            batchList.clear()
                            binding.rvBatch.adapter = null
                            showSnackBar("No settlement data available")
                        }
                    }
                }
            }
        }
    }
}



