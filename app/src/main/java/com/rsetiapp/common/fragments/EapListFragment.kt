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
import com.rsetiapp.databinding.EapListFragmentBinding
import kotlinx.coroutines.launch
import java.util.Calendar

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

        commonViewModel.eapDetailsAPI(EapListReq(BuildConfig.VERSION_NAME, userPreferences.getUseID()))
        collectEapListResponse()
    }

    @SuppressLint("SuspiciousIndentation", "DefaultLocale")
    private fun getValue(eapItem: EapList) {

        eapIdValue = eapItem.eapID
        eapDateValue = eapItem.monthYear // Example: "08/2025"
        eapStatusValue = eapItem.status

        // Get current month and year
        val calendar = Calendar.getInstance()
        val currentMonth = String.format("%02d", calendar.get(Calendar.MONTH) + 1) // Ensure two digits (01, 02, ..., 12)
        val currentYear = calendar.get(Calendar.YEAR).toString()

        // Extract month and year from eapDateValue
        val parts = eapDateValue.split("/")
        val eapMonth = parts[0]  // "08"
        val eapYear = parts[1]   // "2025"

        // Check both conditions: status must be Active & date must match current month/year
        if (eapStatusValue == "Active" && eapMonth == currentMonth && eapYear == currentYear) {
            findNavController().navigate(
                EapListFragmentDirections.actionEapListFragmentToEAPAwarnessFormFragment(
                    formName,
                    eapIdValue
                )
            )
        }
        else if (eapStatusValue == "Expired"){

            AppUtil.showAlertDialog(requireContext(),"Alert","Eap Expired")

        }
        else if (eapStatusValue == "Completed"){

            AppUtil.showAlertDialog(requireContext(),"Alert","Eap Completed")

        }

        else {
            showMismatchAlert(requireContext(), eapMonth, eapYear, currentMonth, currentYear)
        }
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
                            } else {
                                showSnackBar(getEapResponse.responseDesc ?: "Error")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }
    private fun showMismatchAlert(context: Context, eapMonth: String, eapYear: String, currentMonth: String, currentYear: String) {
        AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage("You can only proceed in the month: $eapMonth/$eapYear.\nCurrent date: $currentMonth/$currentYear.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}