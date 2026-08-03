package com.rsetiapp.common.fragments

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.EapAdapter
import com.rsetiapp.common.model.request.EapListReq
import com.rsetiapp.common.model.response.EapList
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.databinding.EapListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



@AndroidEntryPoint
class EapListFragment  : BaseFragment<EapListFragmentBinding>(EapListFragmentBinding::inflate) {
    private val commonViewModel: CommonViewModel by activityViewModels()


    private lateinit var eapAdapter: EapAdapter
    private var eapList: MutableList<EapList> = mutableListOf()
    private var originalEapList: MutableList<EapList> = mutableListOf()

    private var eapIdValue=""
    private var eapStatusValue=""
    private var eapDateValue=""
    private var formName=""
    private var stateNme=""
    private var stateCode=""
    private var districtCode=""
    private var districtName=""
    private var blockName=""
    private var blockCode=""
    private var gpName=""
    private var gpCode=""
    private var villageName=""
    private var villageCode=""
    private var eapName=""
    private var programCode=""
    private var selectedYear = ""
    private var selectedStatus = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())



        init()
    }

    private fun init() {

        val yearAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            getAcademicYears()
        )

        binding.actAcademicYear.setAdapter(yearAdapter)

        binding.actAcademicYear.setOnItemClickListener { parent, view, position, id ->
            selectedYear = parent.getItemAtPosition(position).toString()
            filterList()

        }



        val categoryList = arrayListOf(
            "Active",
            "Completed",
            "Expired"
        )

        binding.actCategory.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryList
            )
        )


        binding.actCategory.setOnItemClickListener { parent, view, position, id ->
            selectedStatus = parent.getItemAtPosition(position).toString()

            filterList()

        }

        commonViewModel.eapDetailsAPI(AppUtil.getSavedTokenPreference(requireContext()),EapListReq(BuildConfig.VERSION_NAME, userPreferences.getUseID(),AppUtil.getAndroidId(requireContext())))
        collectEapListResponse()

        formName = arguments?.getString("formName").toString()
        userPreferences = UserPreferences(requireContext())
        eapAdapter = EapAdapter(eapList) { eapItem ->
            getValue(eapItem)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = eapAdapter


    }

    @SuppressLint("SuspiciousIndentation", "DefaultLocale")
    private fun getValue(eapItem: EapList) {
        eapIdValue = eapItem.eapID.toString()
        eapDateValue = eapItem.monthYear // Example: "24/05/2024"
        eapStatusValue = eapItem.status
        stateNme = eapItem.stateNme
        stateCode = eapItem.stateCode
        districtCode = eapItem.districtCode
        districtName = eapItem.districtName
        blockName = eapItem.blockName
        blockCode = eapItem.blockCode
        gpName = eapItem.gpName
        gpCode = eapItem.gpCode
        villageName = eapItem.villageName
        villageCode = eapItem.villageCode
        eapName = eapItem.eapName
        programCode = eapItem.programCode.toString()


        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        val currentDate = sdf.format(Date()) // Gets today's date in dd/MM/yyyy format

        try {
            val eapDate = sdf.parse(eapDateValue)
            val todayDate = sdf.parse(currentDate)

            if (eapStatusValue == "Active" && eapDate == todayDate) {
                findNavController().navigate(
                    EapListFragmentDirections.actionEapListFragmentToEAPAwarnessFormFragment(
                        formName,
                        eapIdValue,
                        stateNme,stateCode,districtCode,districtName,blockName,blockCode,gpName,gpCode,villageName,villageCode,eapName,programCode
                    )
                )
            } else if (eapStatusValue == "Expired") {
                AppUtil.showAlertDialog(requireContext(), "Alert", "Eap Expired")
            } else if (eapStatusValue == "Completed") {

                AppUtil.showAlertDialog(requireContext(), "Alert", "Eap Completed")
            } else {
                showMismatchAlert(requireContext(), eapDateValue, currentDate)
            }
        } catch (e: ParseException) {
            AppUtil.showAlertDialog(requireContext(), "Error", "Invalid date format in EAP data.")
        }
    }

    private fun showMismatchAlert(context: Context, eapDate: String, currentDate: String) {
        AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage("You can only proceed on the date: $eapDate.\nToday's date: $currentDate.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun collectEapListResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.eapDetailsAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar("Internal Server Error")
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getEapResponse ->
                            when (getEapResponse.responseCode) {
                                200 -> {
                                    eapList.clear()
                                    originalEapList.clear()
                                    originalEapList.addAll(getEapResponse.wrappedList)
                                    eapList.addAll(originalEapList)
                                    eapAdapter.notifyDataSetChanged()
                                }
                                401 -> {
                                    AppUtil.showSessionExpiredDialog(findNavController(), requireContext())
                                }
                                else -> {
                                    toastLong(getEapResponse.responseDesc)
                                }
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun getAcademicYears(): ArrayList<String> {

        val list = ArrayList<String>()
        list.add("All")

        val startYear = 2025
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (year in startYear..currentYear) {
            list.add(year.toString())
        }

        return list
    }


    private fun filterList() {

        val filteredList = originalEapList.filter { item ->

            // monthYear = "24/05/2024"
            val year = try {
                item.monthYear.substringAfterLast("/")
            } catch (e: Exception) {
                ""
            }

            val yearMatch =
                selectedYear.isEmpty() ||
                        selectedYear == "All" ||
                        year == selectedYear

            val statusMatch =
                selectedStatus.isEmpty() ||
                        item.status.equals(selectedStatus, ignoreCase = true)

            yearMatch && statusMatch
        }

        eapList.clear()
        eapList.addAll(filteredList)
        eapAdapter.notifyDataSetChanged()
    }
}