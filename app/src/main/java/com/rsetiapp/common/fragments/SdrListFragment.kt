package com.rsetiapp.common.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.adapter.SdrAdapter
import com.rsetiapp.common.model.request.SdrListReq
import com.rsetiapp.common.model.response.VisitData
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.geoFancing.GeofenceBroadcastReceiver
import com.rsetiapp.core.geoFancing.GeofenceHelper
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.databinding.FragmentSdrListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SdrListFragment : BaseFragment<FragmentSdrListBinding>(FragmentSdrListBinding::inflate) {
    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var geofencingClient: GeofencingClient

    private lateinit var sdrAdapter: SdrAdapter
    private var sdrList: MutableList<VisitData> = mutableListOf()
    private var formName = ""

    // Hold last clicked geofence data to add after permission granted
    private var lastLatLng: Pair<Double, Double>? = null
    private var lastRadius: Float? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false
        } else {
            true
        }

        if (fineLocationGranted && backgroundLocationGranted) {
            lastLatLng?.let { (lat, lng) ->
                lastRadius?.let { radius ->
                    geofenceHelper.addGeofence(lat, lng, radius, "ATTENDANCE_ZONE")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Location permissions are required for geofencing", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())
        formName = arguments?.getString("formName").toString()
        geofenceHelper = GeofenceHelper(requireContext())
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        init()
    }

    private fun init() {
        commonViewModel.getSdrListApi(
            AppUtil.getSavedTokenPreference(requireContext()),
            SdrListReq(
                BuildConfig.VERSION_NAME,
                AppUtil.getAndroidId(requireContext()),
                userPreferences.getUseID()
            )
        )
        collectSdrListResponse()
        setupListeners()
    }

    private fun setupListeners() {
        binding.formText.text = formName
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        sdrAdapter = SdrAdapter(sdrList) { selectedItem ->
            val lat = selectedItem.lattitude?.toDoubleOrNull() ?: return@SdrAdapter
            val lng = selectedItem.longitude?.toDoubleOrNull() ?: return@SdrAdapter
            val radius = 500f

            checkAndRequestPermissions(lat, lng, radius)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sdrAdapter
        }
    }

    private fun checkAndRequestPermissions(lat: Double, lng: Double, radius: Float) {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }

        if (permissionsNeeded.isEmpty()) {
            geofenceHelper.addGeofence(lat, lng, radius, "ATTENDANCE_ZONE")
        } else {
            lastLatLng = Pair(lat, lng)
            lastRadius = radius
            locationPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }

    private fun collectSdrListResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getSdrListApi) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar("Internal Server Error")
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { response ->
                            if (response.responseCode == 200) {
                                sdrList.clear()
                                sdrList.addAll(response.wrappedList)
                                sdrAdapter.notifyDataSetChanged()
                            } else if (response.responseCode == 401) {
                                AppUtil.showSessionExpiredDialog(findNavController(), requireContext())
                            } else {
                                toastLong(response.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }
}
