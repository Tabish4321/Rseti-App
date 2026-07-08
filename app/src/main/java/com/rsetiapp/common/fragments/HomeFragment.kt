package com.rsetiapp.common.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.rsetiapp.BuildConfig
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
import androidx.appcompat.app.AlertDialog

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

        val headerView = binding.navigationView.getHeaderView(0)
        val headerBinding = NavigationHeaderBinding.bind(headerView)

        val headerImageView: ImageView = headerBinding.circleImageView
        val headerIdView: TextView = headerBinding.loginId

        headerIdView.text =
            userPreferences.getUserName() + " (" + userPreferences.getUseID() + ")"

        binding.profilePic.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.changeLanguage.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFrahmentToLanguageChangeFragment()
            )
        }

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_logout -> {
                    toastShort("Logged out")
                    AppUtil.saveLoginStatus(requireContext(), false)
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFrahmentToLoginFragment2()
                    )
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                // 🔥 LAT LONG FEATURE
                R.id.lat_lang -> {

                    val fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1001
                        )
                        return@setNavigationItemSelectedListener true
                    }

                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {

                            val latitude = location.latitude
                            val longitude = location.longitude

                            showLatLongDialog(latitude, longitude)

                        } else {
                            toastShort("Location not found. Please enable GPS")
                        }
                    }
                }
            }
            true
        }
    }

    // 🔥 Dialog Function
    private fun showLatLongDialog(lat: Double, lng: Double) {

        val message = "Latitude: $lat\nLongitude: $lng"

        AlertDialog.Builder(requireContext())
            .setTitle("Your Location 📍")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    // 🔥 Permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            toastShort("Permission Granted, click again")
        } else {
            toastShort("Location Permission Denied")
        }
    }

    private fun setupRecyclerView() {
        parentAdapter = ParentAdapter(moduleList)
        binding.rvParent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParent.adapter = parentAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectModulesData() {
        commonViewModel.getFormAPI(
            AppUtil.getSavedTokenPreference(requireContext()),
            BuildConfig.VERSION_NAME,
            userPreferences.getUseID(),
            AppUtil.getAndroidId(requireContext())
        )

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
                            } else if (response.responseCode == 401) {
                                AppUtil.showSessionExpiredDialog(
                                    findNavController(),
                                    requireContext()
                                )
                            } else {
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
                private val exitInterval = 2000

                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < exitInterval) {
                        isEnabled = false
                        requireActivity().finish()
                    } else {
                        backPressedTime = currentTime
                        showSnackBar("Press back again to exit")
                    }
                }
            })
    }
}