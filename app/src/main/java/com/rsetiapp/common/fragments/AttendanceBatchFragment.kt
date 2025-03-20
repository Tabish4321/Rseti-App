package com.rsetiapp.common.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.AttendanceBatchAdapter
import com.rsetiapp.common.model.response.AttendanceBatch
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.Resource
import com.rsetiapp.databinding.AttendanceBatchFragmentBinding
import kotlinx.coroutines.launch

class AttendanceBatchFragment :
    BaseFragment<AttendanceBatchFragmentBinding>(AttendanceBatchFragmentBinding::inflate)  {
    private lateinit var batchAdapter: AttendanceBatchAdapter
    private var AttendanceBatchList = mutableListOf<AttendanceBatch>()
    private val commonViewModel: CommonViewModel by activityViewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupRecyclerView()

    }

    private fun init(){

        collectAttendanceBatchResponse()
        commonViewModel.getAttendanceBatchAPI(BuildConfig.VERSION_NAME)
        listener()

    }
        private fun listener() {

            binding.backButton.setOnClickListener {
                findNavController().navigateUp()
            }

        }


    private fun setupRecyclerView() {
        batchAdapter = AttendanceBatchAdapter(AttendanceBatchList)
        binding.recyclerViewBatches.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBatches.adapter = batchAdapter

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun collectAttendanceBatchResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getAttendanceBatchAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar("Internal Server Error111")
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getAttendanceBatchAPI ->
                            if (getAttendanceBatchAPI.responseCode == 200) {
                                AttendanceBatchList.clear()
                                AttendanceBatchList.addAll(getAttendanceBatchAPI.wrappedList) // Add new data
                                batchAdapter.notifyDataSetChanged()
                            } else if (getAttendanceBatchAPI.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }


    }