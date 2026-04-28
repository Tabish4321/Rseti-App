package com.rsetiapp.common.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.SettlementBatchAdapter
import com.rsetiapp.common.model.request.DistrictListReq
import com.rsetiapp.common.model.request.InstituteListReq
import com.rsetiapp.common.model.request.SettlementVeryficationBatchReq
import com.rsetiapp.common.model.response.DistrictList
import com.rsetiapp.common.model.response.Institutes
import com.rsetiapp.common.model.response.SettlementPercentage
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.databinding.FragmentSettlementVeryficationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch




@AndroidEntryPoint
class SettlementVeryficationFragment :
    BaseFragment<FragmentSettlementVeryficationBinding>(FragmentSettlementVeryficationBinding::inflate) {

    private lateinit var DistrictAdapter: ArrayAdapter<String>
    private lateinit var InstituteAdapter: ArrayAdapter<String>

    private var DistrictList: List<DistrictList> = mutableListOf()
    private var InstituteList: List<Institutes> = mutableListOf()

    private var DistrictName = ArrayList<String>()
    private var InstituteName = ArrayList<String>()

    private val commonViewModel: CommonViewModel by activityViewModels()

    private val batchList = mutableListOf<SettlementPercentage>()

    private val settlementVeryfiationAdapter by lazy {
        SettlementBatchAdapter(batchList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        collectBatchesData() // ✅ ONLY ONCE
        init()
          val dataRecy=AppUtil.getSavedRecyclerViewPreference(requireContext())
        if (dataRecy.equals("true")){

            binding.rvBatch.visibility=View.GONE
        }
           else{

            binding.rvBatch.visibility=View.VISIBLE

           }




    }

    private fun init() {

        binding.tvTitleName.text = getString(R.string.settlement_batch)

//        binding.backButton.setOnClickListener {
//            findNavController().navigateUp()
//        }

        binding.backButton.setOnClickListener {
            AppUtil.saveRecyclerViewPreference(requireContext(), "false")
            DistrictList = emptyList()
            InstituteList = emptyList()
            DistrictName.clear()
            InstituteName.clear()
            DistrictAdapter.notifyDataSetChanged()
            InstituteAdapter.notifyDataSetChanged()
            binding.spinnerDistrict.setText("", false)
            binding.spinnerInstitute.setText("", false)
            batchList.clear()
            settlementVeryfiationAdapter.notifyDataSetChanged()
            binding.rvBatch.visibility = View.GONE
            findNavController().navigateUp()
        }

        DistrictAdapter()
        Institutelistner()

        val value = AppUtil.getSavedEntityPreference(requireContext())
        val lastTwoDigits = value.takeLast(2)

        commonViewModel.getdistrictListAPI(
            DistrictListReq(lastTwoDigits, BuildConfig.VERSION_NAME)
        )

        collectFollowTypeResponse()
    }

    private fun setupAdapter() {
        binding.rvBatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBatch.adapter = settlementVeryfiationAdapter
        binding.rvBatch.visibility = View.GONE // Initially hide RecyclerView
    }

    // ✅ FIX: NO collectBatchesData() here
    private fun Institutelistner() {

        InstituteAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            InstituteName
        )

        binding.spinnerInstitute.setAdapter(InstituteAdapter)

        binding.spinnerInstitute.setOnItemClickListener { _, _, position, _ ->

            val selected = InstituteList[position]

            // 🔥 clear old data (avoid duplicate)
            batchList.clear()
            settlementVeryfiationAdapter.notifyDataSetChanged()

            // ✅ only API call
            commonViewModel.getsettledbatchAPI(
                SettlementVeryficationBatchReq(
                    BuildConfig.VERSION_NAME,
                    selected.instituteId
                )
            )
            AppUtil.saveinstituteIdPreference(requireContext(),selected.instituteId)
        }
    }

    private fun DistrictAdapter() {

        DistrictAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            DistrictName
        )

        binding.spinnerDistrict.setAdapter(DistrictAdapter)

        binding.spinnerDistrict.setOnItemClickListener { _, _, position, _ ->

            val selected = DistrictList[position]

            InstituteName.clear()
            InstituteAdapter.notifyDataSetChanged()
            binding.spinnerInstitute.setText("", false)

            val request = InstituteListReq(
                appVersion = BuildConfig.VERSION_NAME,
                login = UserPreferences(requireContext()).getUseID(),
                imeiNo = AppUtil.getAndroidId(requireContext()),
                districtCode = selected.districtCode
            )

            commonViewModel.instituteListAPI(
                token = AppUtil.getSavedTokenPreference(requireContext())
                    .removePrefix("Bearer ")
                    .trim(),
                request = request
            )

            InstituteListResponse()
        }
    }

    private fun InstituteListResponse() {

        viewLifecycleOwner.lifecycleScope.launch {
            commonViewModel.instituteListAPI.collectLatest { resource ->

                when (resource) {

                    is Resource.Loading -> showProgressBar()

                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(resource.error?.message ?: "Error")
                    }

                    is Resource.Success -> {
                        hideProgressBar()

                        val list = resource.data?.wrappedList

                        if (!list.isNullOrEmpty()) {

                            InstituteList = list

                            InstituteName.clear()
                            InstituteName.addAll(list.map { it.instituteName })

                            InstituteAdapter.notifyDataSetChanged()

                        } else {
                            showSnackBar("No institute data available")
                        }
                    }
                }
            }
        }
    }

    private fun collectFollowTypeResponse() {

        viewLifecycleOwner.lifecycleScope.launch {
            commonViewModel.districtList.collectLatest { resource ->

                when (resource) {

                    is Resource.Loading -> showProgressBar()

                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(resource.error?.message ?: "Error")
                    }

                    is Resource.Success -> {
                        hideProgressBar()

                        val list = resource.data?.districtList

                        if (!list.isNullOrEmpty()) {

                            DistrictList = list

                            DistrictName.clear()
                            DistrictName.addAll(list.map { it.districtName })

                            DistrictAdapter.notifyDataSetChanged()

                        } else {
                            showSnackBar("No district data available")
                        }
                    }
                }
            }
        }
    }

    // 🔥 MAIN FIX
    private fun collectBatchesData() {

        viewLifecycleOwner.lifecycleScope.launch {
            commonViewModel.getsettledbatchAPI.collectLatest { resource ->

                when (resource) {

                    is Resource.Loading -> showProgressBar()

                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(resource.error?.message ?: "Error")
                    }

                    is Resource.Success -> {
                        hideProgressBar()

                        val list = resource.data?.wrappedList ?: emptyList()

                        if (list.isNotEmpty()) {

                            val uniqueList = list.distinctBy { it.batchId }

                            batchList.clear()
                            batchList.addAll(uniqueList)

                            settlementVeryfiationAdapter.notifyDataSetChanged()

                            binding.rvBatch.visibility = View.VISIBLE // Show RecyclerView when data is available

                        } else {
                            batchList.clear()
                            settlementVeryfiationAdapter.notifyDataSetChanged()

                            binding.rvBatch.visibility = View.GONE // Hide RecyclerView when no data
                        }
                    }
                }
            }
        }
    }
    fun updateList(newList: List<SettlementPercentage>) {
        batchList.clear()
        batchList.addAll(newList)
        settlementVeryfiationAdapter. notifyDataSetChanged()
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // ✅ Only when leaving screen completely
        batchList.clear()
    }
}
