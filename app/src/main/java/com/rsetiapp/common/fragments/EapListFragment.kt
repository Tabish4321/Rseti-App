package com.rsetiapp.common.fragments

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
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.databinding.EapListFragmentBinding
import kotlinx.coroutines.launch

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

    private fun getValue(eapItem: EapList) {

      eapIdValue =  eapItem.eapID
      eapDateValue =  eapItem.monthYear
      eapStatusValue = eapItem.status

        findNavController().navigate(EapListFragmentDirections.actionEapListFragmentToEAPAwarnessFormFragment(formName,eapIdValue))


    }



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

}