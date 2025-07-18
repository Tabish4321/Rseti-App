package com.rsetiapp.common.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.ParentAdapter
import com.rsetiapp.common.model.response.Module
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.FragmentHomeBinding
import com.rsetiapp.databinding.NavigationHeaderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var parentAdapter: ParentAdapter
    private val moduleList = mutableListOf<Module>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        setupRecyclerView()
        collectModulesData()
        handleBackPress()


        // First, get the header view using getHeaderView()
        val headerView = binding.navigationView.getHeaderView(0)

        // Now, bind the header layout using the generated ViewBinding for the header
        val headerBinding = NavigationHeaderBinding.bind(headerView)

        // Access the ImageView from the header layout
        val headerImageView: ImageView = headerBinding.circleImageView
        val headerIdView: TextView = headerBinding.loginId

        headerIdView.text = userPreferences.getUserName()+  " ("+userPreferences.getUseID()+")"

        binding.profilePic.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.changeLanguage.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFrahmentToLanguageChangeFragment())

        }
        // Handle item selection in the navigation menu
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout-> {
                    Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                    AppUtil.saveLoginStatus(requireContext(), false)

                    findNavController().navigate(HomeFragmentDirections.actionHomeFrahmentToLoginFragment2())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }


    }


    private fun setupRecyclerView() {
        parentAdapter = ParentAdapter(moduleList)
        binding.rvParent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParent.adapter = parentAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectModulesData() {
        commonViewModel.getFormAPI(AppUtil.getSavedTokenPreference(requireContext()),BuildConfig.VERSION_NAME,userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))
        lifecycleScope.launch {
            commonViewModel.getFormAPI.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        resource.error?.message?.let { toastShort(it) }
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        resource.data?.let { response ->
                            if (response.responseCode == 200) {
                                moduleList.clear()
                                moduleList.addAll(response.wrappedList)
                                parentAdapter.notifyDataSetChanged()
                            }
                            else if (response.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(response.responseDesc)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                private var backPressedTime: Long = 0
                private val exitInterval = 2000 // 2 seconds

                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < exitInterval) {
                        isEnabled =
                            false // Disable callback to let the system handle the back press
                        requireActivity().finish()
                    } else {
                        backPressedTime = currentTime
                        showSnackBar("Press back again to exit")
                    }
                }
            })
    }

}
