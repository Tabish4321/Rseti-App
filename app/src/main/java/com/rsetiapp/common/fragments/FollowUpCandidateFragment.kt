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
import com.rsetiapp.common.adapter.CandidateDetailsAdapter
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.Resource
import com.rsetiapp.databinding.FragmentFollowUpCandidateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowUpCandidateFragment :
    BaseFragment<FragmentFollowUpCandidateBinding>(FragmentFollowUpCandidateBinding::inflate) {

    private var batchId = ""
    private var batchName = ""
    private lateinit var candidateAdapter: CandidateDetailsAdapter
    private val candidateList = mutableListOf<CandidateDetail>()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        batchId = arguments?.getString("batchId").toString()
        batchName = arguments?.getString("batchName").toString()

        init()
        setupRecyclerView()
        collectCandidatesData()
    }

    private fun init() {
        binding.tvBatchName.text = batchName
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        candidateAdapter = CandidateDetailsAdapter(candidateList)
        binding.rvCandidate.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCandidate.adapter = candidateAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectCandidatesData() {
        commonViewModel.getCandidateAPI(
            BuildConfig.VERSION_NAME,
            batchId
        )
        lifecycleScope.launch {
            commonViewModel.getCandidateAPI.collectLatest { resource ->
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
                        resource.data?.let { getCandidateResponse ->
                            if (getCandidateResponse.responseCode == 200) {
                                candidateList.clear()
                                candidateList.addAll(getCandidateResponse.wrappedList)
                                candidateAdapter.notifyDataSetChanged()
                            } else if (getCandidateResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            } else {
                                showSnackBar(getCandidateResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }
}