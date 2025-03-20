package com.rsetiapp.common.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.BatchAdapter
import com.rsetiapp.common.model.response.Batch
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil.convertMonthNumberToFullName
import com.rsetiapp.core.util.AppUtil.extractMonthFromDate
import com.rsetiapp.core.util.AppUtil.extractYearFromDate
import com.rsetiapp.core.util.AppUtil.getCurrentYear
import com.rsetiapp.core.util.Resource
import com.rsetiapp.databinding.FragmentFollowUpBatchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowUpBatchFragment :
    BaseFragment<FragmentFollowUpBatchBinding>(FragmentFollowUpBatchBinding::inflate) {

    private var formName = ""
    private lateinit var batchAdapter: BatchAdapter
    private val batchList = mutableListOf<Batch>()
    private val batchFilteredList = mutableListOf<Batch>()
    private val commonViewModel: CommonViewModel by activityViewModels()

    private lateinit var yearAdapter: ArrayAdapter<String>
    private var years = ArrayList<String>()
    private var selectedYear = ""

    private lateinit var monthAdapter: ArrayAdapter<String>
    private var months = ArrayList<String>()
    private var selectedMonth = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formName = arguments?.getString("formName").toString()

        init()
        collectBatchesData()
    }

    private fun init() {
        binding.tvTitleName.text = formName
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        years =
            (arrayListOf("All") + (2023..getCurrentYear()).map { it.toString() }) as ArrayList<String>
        months =
            (arrayListOf("All") + (1..12).map { convertMonthNumberToFullName(it) }) as ArrayList<String>

        setupRecyclerView()
        listener()
    }

    private fun listener() {
        yearAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, years
        )
        binding.spinnerYear.setAdapter(yearAdapter)
        selectedYear = years[0]


        monthAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, months
        )
        binding.spinnerMonth.setAdapter(monthAdapter)
        selectedMonth = months[0]


        binding.spinnerYear.setOnItemClickListener { parent, view, position, id ->
            selectedYear = parent.getItemAtPosition(position).toString()
            updateBatchList()
        }

        binding.spinnerMonth.setOnItemClickListener { parent, view, position, id ->
            selectedMonth = parent.getItemAtPosition(position).toString()
            updateBatchList()
        }
    }

    private fun updateBatchList() {
        batchFilteredList.clear()
        batchFilteredList.addAll(batchList.filter { batch: Batch ->
            when {
                selectedYear == "All" -> if (selectedMonth == "All") true else (extractMonthFromDate(
                    batch.complitionDate
                ).equals(selectedMonth, ignoreCase = true))

                selectedMonth == "All" -> (extractYearFromDate(batch.complitionDate) == selectedYear)
                else -> ((extractYearFromDate(batch.complitionDate) == selectedYear) && (extractMonthFromDate(
                    batch.complitionDate
                ).equals(selectedMonth, ignoreCase = true)))
            }
        })
        batchAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        batchAdapter = BatchAdapter(batchFilteredList)
        binding.rvBatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBatch.adapter = batchAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectBatchesData() {
        commonViewModel.getBatchAPI(BuildConfig.VERSION_NAME, userPreferences.getUseID())
        lifecycleScope.launch {
            commonViewModel.getBatchAPI.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        resource.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        resource.data?.let { getBatchResponse ->
                            if (getBatchResponse.responseCode == 200) {
                                batchList.clear()
                                batchList.addAll(getBatchResponse.wrappedList)

                                batchFilteredList.clear()
                                batchFilteredList.addAll(batchList)

                                batchAdapter.notifyDataSetChanged()
                            } else if (getBatchResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            } else {
                                showSnackBar(getBatchResponse.responseDesc ?: "")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }
}