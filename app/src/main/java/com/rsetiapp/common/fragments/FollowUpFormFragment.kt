package com.rsetiapp.common.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.FollowUpInsertReq
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.common.model.response.FollowUpStatus
import com.rsetiapp.common.model.response.FollowUpType
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil.getCurrentDate
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.core.util.visible
import com.rsetiapp.databinding.FragmentFollowUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale

@AndroidEntryPoint
class FollowUpFormFragment :
    BaseFragment<FragmentFollowUpBinding>(FragmentFollowUpBinding::inflate) {

    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var candidate: CandidateDetail

    //Follow Up Type var
    private var followUpTypeList: List<FollowUpType> = mutableListOf()
    private var followUpTypeName = ArrayList<String>()
    private lateinit var followUpTypeAdapter: ArrayAdapter<String>
    private var selectedFollowUpType: FollowUpType? = null

    //Follow Up Status var
    private var followUpStatusList: List<FollowUpStatus> = mutableListOf()
    private var followUpStatusName = ArrayList<String>()
    private lateinit var followUpStatusAdapter: ArrayAdapter<String>
    private var selectedFollowUpStatus: FollowUpStatus? = null

    private var image1Base64 = ""
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var locationLatLang = ""
    private var locationAddress = ""
    private var imageUri: Uri? = null
    private var currentImageView: ImageView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var reason: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        candidate = arguments?.getSerializable("candidate") as CandidateDetail
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        userPreferences = UserPreferences(requireContext())

        if (candidate.candidateProfilePic == "NA") {
            Glide.with(binding.root.context).load(R.drawable.person).into(binding.candidateImage)
        } else {
            val decodedString: ByteArray =
                Base64.decode(candidate.candidateProfilePic, Base64.DEFAULT)
            val profileBitmap: Bitmap =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            Glide.with(binding.root.context).load(profileBitmap).into(binding.candidateImage)
        }
        binding.tvCandidateName.text = candidate.candidateName
        binding.tvCareOfName.text = candidate.guardianName
        binding.tvContactName.text = candidate.mobileNo

        commonViewModel.getFollowTypeListAPI()
        collectFollowTypeResponse()
        commonViewModel.getFollowStatusListAPI()
        collectFollowStatusResponse()

        binding.tvDate.text = getCurrentDate()
        binding.etDoneBy.text = userPreferences.getUserName()

        listener()
    }

    private fun listener() {
        // Back Button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        //Adapter Follow Up Type
        followUpTypeAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, followUpTypeName
        )
        binding.spinnerFollowType.setAdapter(followUpTypeAdapter)
        binding.spinnerFollowType.setOnItemClickListener { parent, view, position, id ->
            selectedFollowUpType = followUpTypeList[position]
        }

        //Adapter Follow Up Status
        followUpStatusAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, followUpStatusName
        )
        binding.spinnerStatus.setAdapter(followUpStatusAdapter)
        binding.spinnerStatus.setOnItemClickListener { parent, view, position, id ->
            selectedFollowUpStatus = followUpStatusList[position]

            when (selectedFollowUpStatus!!.statusId) {
                1 -> {
                    binding.tvReason.text = getString(R.string.reason_nm)
                    binding.tvReason.visibility = View.VISIBLE
                    binding.etReason.visibility = View.VISIBLE

                    binding.etReason.text.clear()
                    reason = ""
                }

                3 -> {
                    binding.tvReason.text = getString(R.string.reason)
                    binding.tvReason.visibility = View.VISIBLE
                    binding.etReason.visibility = View.VISIBLE

                    binding.etReason.text.clear()
                    reason = ""
                }

                else -> {
                    binding.tvReason.visibility = View.GONE
                    binding.etReason.visibility = View.GONE

                    binding.etReason.text.clear()
                    reason = ""
                }
            }
        }

        // Capture Image
        binding.image1.setOnClickListener {
            openCamera(binding.image1)
        }

        //Submit Button
        binding.btnSubmit.setOnClickListener {
            reason = binding.etReason.text.toString()

            if (selectedFollowUpType != null && selectedFollowUpStatus != null && (if (selectedFollowUpStatus!!.statusId == 3) reason.isNotEmpty() else true) && image1Base64.isNotEmpty()) {
                commonViewModel.insertFollowUpAPI(
                    FollowUpInsertReq(
                        appVersion = BuildConfig.VERSION_NAME,
                        batchId = candidate.batchId.toString(),
                        candidateId = candidate.candidateId ?: "",
                        mobileNo = candidate.mobileNo ?: "",
                        guardianName = candidate.guardianName ?: "",
                        guardianMobileNo = candidate.guardianNo ?: "",
                        candidatePhoto = candidate.candidateProfilePic ?: "",
                        quarterOne = candidate.quarter1 ?: "",
                        quarterTwo = candidate.quarter2 ?: "",
                        quarterThree = candidate.quarter3 ?: "",
                        quarterFour = candidate.quarter4 ?: "",
                        quarterFive = candidate.quarter5 ?: "",
                        quarterSix = candidate.quarter6 ?: "",
                        quarterSeven = candidate.quarter7 ?: "",
                        quarterEight = candidate.quarter8 ?: "",
                        userId = userPreferences.getUserName(),
                        followUpType = selectedFollowUpType!!.followupTypeId,
                        followUpdate = getCurrentDate(),
                        followUpDoneBy = userPreferences.getUseID(),
                        sattlementStatus = selectedFollowUpStatus!!.statusId.toString(),
                        reason = reason,
                        followupimage = image1Base64,
                        latitute = latitude.toString(),
                        longitute = longitude.toString()
                    )
                )
                collectInsertResponse()
            } else toastShort("Kindly fill all the fields first")
        }
    }

    private fun collectFollowTypeResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getFollowTypeList) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            toastShort(baseErrorResponse.message)
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getFollowUpTypeList ->
                            if (getFollowUpTypeList.responseCode == 200) {
                                followUpTypeList = getFollowUpTypeList.wrappedList

                                for (x in followUpTypeList) {
                                    followUpTypeName.add(x.followUpTypeName)
                                }

                                followUpTypeAdapter.notifyDataSetChanged()
                            } else if (getFollowUpTypeList.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            } else {
                                showSnackBar(getFollowUpTypeList.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun collectFollowStatusResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getFollowStatusList) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            toastShort(baseErrorResponse.message)
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getFollowUpStatusList ->
                            if (getFollowUpStatusList.responseCode == 200) {
                                followUpStatusList = getFollowUpStatusList.wrappedList

                                for (x in followUpStatusList) {
                                    followUpStatusName.add(x.status)
                                }

                                followUpStatusAdapter.notifyDataSetChanged()
                            } else if (getFollowUpStatusList.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            } else {
                                showSnackBar(getFollowUpStatusList.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun collectInsertResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.insertFollowUpAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { insertApiResp ->
                            if (insertApiResp.responseCode == 200) {
                                showSnackBar("Success")
                                findNavController().navigateUp()
                            } else if (insertApiResp.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            } else {
                                showSnackBar("Internal Server Error 1111")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissions.any {
                ContextCompat.checkSelfPermission(
                    requireContext(), it
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 100)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera(imageView: ImageView) {
        checkAndRequestPermissions()

        currentImageView = imageView  // ✅ Store the clicked ImageView

        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Camera permission required!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.provider", photoFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        cameraLauncher.launch(intent) // ✅ Launch the camera
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${System.currentTimeMillis()}", ".jpg", storageDir).apply {
            imageUri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.provider", this
            )
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (imageUri == null || currentImageView == null) {
                    Toast.makeText(requireContext(), "Image capture failed!", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                }

                val bitmap = uriToBitmap(imageUri!!)
                bitmap?.let { compressedBitmap ->
                    currentImageView?.setImageBitmap(compressedBitmap) // ✅ Set the image

                    val base64Image = bitmapToBase64(compressedBitmap) // Convert to Base64

                    // ✅ Check which ImageView was clicked and store Base64 accordingly
                    when (currentImageView?.id) {
                        R.id.image1 -> {
                            image1Base64 = base64Image

                        }
                    }

                    getCurrentLocation()
                    binding.lllatLang.visible()
                    binding.llAdress.visible()
                }
            }
        }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            compressBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        return try {
            val maxSize = 1024 // Resize to max 1024px width/height
            val width = bitmap.width
            val height = bitmap.height
            val scale =
                if (width > height) maxSize.toFloat() / width else maxSize.toFloat() / height

            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap // Return the original bitmap if compression fails
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // Increase quality to 90
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            Base64.encodeToString(byteArray, Base64.NO_WRAP) // Use NO_WRAP to avoid line breaks
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude

                locationLatLang = "Lat: $latitude, Lng: $longitude"
                binding.location.text = locationLatLang

                // Fetch and update address
                getAddressFromLocation(latitude!!, longitude!!)
            } else {
                // If last known location is null, request a fresh location update
                requestNewLocation()
            }
        }.addOnFailureListener {
            binding.location.text = getString(R.string.location_not_found)
            binding.address.text = getString(R.string.address_not_found)
            Log.e("LocationError", "Failed to get location: ${it.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            5000 // Update every 5 sec
        ).apply {
            setWaitForAccurateLocation(true) // Ensures accuracy
            setMinUpdateIntervalMillis(2000) // Minimum update interval
            setMaxUpdateDelayMillis(10000) // Max delay
            setMaxUpdates(1) // Get only one update and stop
        }.build()

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let { location ->
                    latitude = location.latitude
                    longitude = location.longitude

                    locationLatLang = "Lat: $latitude, Lng: $longitude"
                    binding.location.text = locationLatLang

                    // Fetch and update address
                    getAddressFromLocation(latitude!!, longitude!!)

                    fusedLocationClient.removeLocationUpdates(this) // Stop updates after getting location
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale("en", "IN"))
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: "Address not available"
                val city = address.locality ?: "Unknown City"
                val state = address.adminArea ?: "Unknown State"
                val pincode = address.postalCode ?: "No Pincode"
                val country = address.countryName ?: "Unknown Country"

                locationAddress = "$fullAddress, $city, $state, $pincode, $country"
                binding.address.text = locationAddress
            } else {
                binding.address.text = getString(R.string.address_not_found)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.address.text = getString(R.string.error_address)
        }
    }
}