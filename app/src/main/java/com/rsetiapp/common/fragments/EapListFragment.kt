package com.rsetiapp.common.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
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
    private var eapIdValue=""
    private var eapStatusValue=""
    private var eapDateValue=""
    private var formName=""




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())



        init()
    }

    private fun init() {
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

        commonViewModel.eapDetailsAPI(AppUtil.getSavedTokenPreference(requireContext()),EapListReq(BuildConfig.VERSION_NAME, userPreferences.getUseID(),AppUtil.getAndroidId(requireContext())))
        collectEapListResponse()
    }

    @SuppressLint("SuspiciousIndentation", "DefaultLocale")
    private fun getValue(eapItem: EapList) {
        eapIdValue = eapItem.eapID
        eapDateValue = eapItem.monthYear // Example: "24/05/2024"
        eapStatusValue = eapItem.status

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
                        eapIdValue
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
                            if (getEapResponse.responseCode == 200) {
                                eapList.clear()  // Clear old data
                                eapList.addAll(getEapResponse.wrappedList)  // Add new data
                                eapAdapter.notifyDataSetChanged()  // Notify RecyclerView
                            }  else if (getEapResponse.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getEapResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

}