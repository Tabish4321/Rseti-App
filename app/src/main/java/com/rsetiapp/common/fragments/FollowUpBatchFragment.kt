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
import com.rsetiapp.common.adapter.BatchAdapter
import com.rsetiapp.common.model.response.Batch
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.toastShort
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
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formName = arguments?.getString("formName").toString()

        init()
        setupRecyclerView()
        collectBatchesData()
    }

    private fun init() {
        binding.tvFormName.text = formName
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        batchAdapter = BatchAdapter(batchList)
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
                                batchAdapter.notifyDataSetChanged()
                            } else if (getBatchResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }
}