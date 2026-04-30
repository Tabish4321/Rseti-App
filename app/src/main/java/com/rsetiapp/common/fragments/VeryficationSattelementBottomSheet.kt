
package com.rsetiapp.common.fragments

import android.Manifest

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rsetiapp.R
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.AppUtil.hasStoragePermission
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.toastLong
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale


import android.graphics.PorterDuff
import android.os.Looper
import android.widget.RadioGroup
import androidx.annotation.RequiresPermission
import androidx.constraintlayout.motion.widget.Debug.getLocation
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.karumi.dexter.BuildConfig
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.SettlementVeryficationUploadReq
import com.rsetiapp.common.model.response.SettlementVeryficationUploadInsertRes
import kotlin.apply
import kotlin.collections.all
import kotlin.collections.any
import kotlin.collections.isNotEmpty
import kotlin.text.toDoubleOrNull


import android.annotation.SuppressLint

import com.google.android.gms.location.*
class VeryficationSattelementBottomSheet() : BottomSheetDialogFragment() {
    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var Bindinglatitude = 0.0
    private var Bindinglongitutde = 0.0
    private var isBottomSheetOpened = false


    private var address: String? = null

    //    private var Bindinglatitude = 27.034750
//    private var Bindinglongitutde = 79.487056
    private var latitude = 0.0
    private var longitude = 0.0
    private var currentImageView: ImageView? = null
    private var image1Base64 = ""
    var radius: Float = 100f

    private val progress: androidx.appcompat.app.AlertDialog? by lazy {
        AppUtil.getProgressDialog(context)
    }
    private val neededPermissions = arrayOf(Manifest.permission.CAMERA)
    private var settlementReason = ""
    private var accountStatus = ""
    private var statusName = ""
    private var salaryRangeId = ""
    private var EditRemark = ""
    private var followupType = ""
    private var branchName = ""
    private var bankName = ""
    private var familyMemberPartTime = ""
    private var employmentGiven = ""
    private var salaryRange = ""
    private var cityName = ""
    private var instituteId = ""
    private var candidateId = ""
    private var candidateName = ""
    private var mobileNo = ""
    private var guardianName = ""
    private var guardianMobileNo = ""
    private var aadharBlockName = ""
    private var aadharPinCode = ""
    private var settlementId = ""
    private var followUpId = ""
    private var batchId = ""
    var isBoardingLoadingProvided: String? = null
    private var ifscCode = ""
    private var loanAccountNo = ""
    private var creditFromBank = ""
    private var selfInvestment = ""
    private var totalInvestment = ""
    private var passbookCopy = ""
    private var appointmentLetterValue = ""
    private var updatedBy = ""
    private var rollNo = ""
    private lateinit var TvRemark: EditText


    private lateinit var tvlattitude: TextView
    private lateinit var tvlong: TextView
    private lateinit var btnSettledSubmit: TextView
    private lateinit var  textViewRemark: TextView
    private var settlmentPhoto = ""
    private lateinit var image1: ImageView
    private lateinit var passbookPhoto: ImageView
    private lateinit var appointmentLetterImage: ImageView
    private lateinit var settlmentPhotoImage: ImageView
    lateinit var userPreferences: UserPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.veryfication_settlement_bottomsheet_layout,
            container,
            false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())



        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // ❌ Permission nahi hai
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // ✅ Permission mil gayi
            getLocation()
        }



        val item = AppUtil.getItem(requireContext())

        if (item != null) {

            instituteId = item.instituteId
            candidateId = item.candidateId
            candidateName = item.candidateName
            mobileNo = item.mobileNo
            guardianName = item.guardianName
            guardianMobileNo = item.guardianMobileNo
            aadharBlockName = item.aadharBlockName
            aadharPinCode = item.aadharPinCode
            settlementId = item.settlementId
            followUpId = item.followUpId
            batchId = item.batchId
            ifscCode = item.ifscCode
            loanAccountNo = item.loanAccountNo
            creditFromBank = item.creditFromBank
            selfInvestment = item.selfInvestment
            totalInvestment = item.totalInvestment
            passbookCopy = item.passbookCopy
            appointmentLetterValue = item.appointmentLetter
            settlmentPhoto = item.settlementPhoto
            updatedBy = item.updatedBy

            Bindinglatitude = item.latitude.toDoubleOrNull() ?: 0.0
            Bindinglongitutde = item.longitude.toDoubleOrNull() ?: 0.0

            rollNo = item.rollNo
            cityName = item.cityName
            settlementReason = item.settlementReason
            accountStatus = item.accountStatus
            salaryRange = item.salaryRange
            employmentGiven = item.employmentGiven
            familyMemberPartTime = item.familyMemberPartTime
            bankName = item.bankName
            branchName = item.branchName
            followupType = item.followupType
            statusName = item.statusName
            salaryRangeId = item.salaryRangeId
        }



        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val tvTitleName = view.findViewById<TextView>(R.id.tvTitleName)
        tvTitleName.setText(candidateName)

        backButton.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.black),
            PorterDuff.Mode.SRC_IN
        )

        backButton.setOnClickListener { dismiss()
//            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnSettledSubmit = view.findViewById<TextView>(R.id.btnSettled)
        passbookPhoto = view.findViewById<ImageView>(R.id.passbookPhoto)
        appointmentLetterImage = view.findViewById<ImageView>(R.id.appointmentLetter)
        settlmentPhotoImage = view.findViewById<ImageView>(R.id.settlmentPhoto)

        tvlattitude = view.findViewById<TextView>(R.id.tvlatlong)
        tvlong = view.findViewById<TextView>(R.id.tvlong)
        TvRemark = view.findViewById<EditText>(R.id.TvRemark)
        val tVstatus = view.findViewById<TextView>(R.id.tVstatus)
        val tvIfscCode = view.findViewById<TextView>(R.id.tvIfscCode)
        val tvLoanBankAcNo = view.findViewById<TextView>(R.id.tvLoanBankAcNo)
        val TvCity = view.findViewById<TextView>(R.id.TvCity)
        val tvBranchName = view.findViewById<TextView>(R.id.tvBranchName)
        val tvBankName = view.findViewById<TextView>(R.id.tvBankName)
        val tvReason = view.findViewById<TextView>(R.id.tvReason)
        val tvAccountStatus = view.findViewById<TextView>(R.id.tvAccountStatus)
        val tvEarningsIncome = view.findViewById<TextView>(R.id.tvEarningsIncome)
        val etEmploymentGiven = view.findViewById<TextView>(R.id.tvEmploymentGiven)
         textViewRemark = view.findViewById<TextView>(R.id.textViewRemark)
        val tvFamilyMemberWorksPartTime =
            view.findViewById<TextView>(R.id.tvFamilyMemberWorksPartTime)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupAreBoardingAandLoadingFacilitiesProvidedYesNo)
// 👇 Listener lagao (IMPORTANT)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                R.id.radioAreBoardingAandLoadingFacilitiesProvidedYes -> {
                    isBoardingLoadingProvided = "Yes"
                    TvRemark.visibility = View.GONE
                    textViewRemark.visibility = View.GONE
                }

                R.id.radioAreBoardingAandLoadingFacilitiesProvidedNo -> {
                    isBoardingLoadingProvided = "No"
                    TvRemark.visibility = View.VISIBLE
                    textViewRemark.visibility = View.VISIBLE
                }
            }
        }
        tVstatus.setText(statusName)
        tvIfscCode.setText(ifscCode)
        tvLoanBankAcNo.setText(loanAccountNo)
        TvCity.setText(cityName)
        tvBranchName.setText(branchName)
        tvBankName.setText(bankName)
        tvReason.setText(settlementReason)
        tvAccountStatus.setText(accountStatus)
        etEmploymentGiven.setText(employmentGiven)
        tvFamilyMemberWorksPartTime.setText(familyMemberPartTime)
        tvEarningsIncome.setText(salaryRangeId)


        val passCopy = passbookCopybase64ToBitmap(passbookCopy)
        val appointmentLetter = base64ImageppointmentLetterbase64ToBitmap(appointmentLetterValue)
        val settlmentPhoto = base64ToBitmap(settlmentPhoto)


        if (passCopy != null) {
            passbookPhoto.setImageBitmap(passCopy)
        }
        if (appointmentLetter != null) {
            appointmentLetterImage.setImageBitmap(appointmentLetter)
        }

        if (settlmentPhoto != null) {
            settlmentPhotoImage.setImageBitmap(settlmentPhoto)
        }

        Log.e("passbookPhoto",passbookPhoto.toString() )
        Log.e("appointmentLetterImage",appointmentLetterImage.toString() )
        Log.e("settlmentPhotoImage",settlmentPhotoImage.toString() )


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkAndRequestStoragePermissions()


        image1 = view.findViewById<ImageView>(R.id.image1)
        image1.setOnClickListener {
//            openCamera()
            openCamera(image1)
        }
        btnSettledSubmit.setOnClickListener {






            checkLocationPermission()

            getCurrentLocation { location ->
                if (location != null) {
                    val isInside = isUserInsideGeofence(location, Bindinglatitude, Bindinglongitutde, radius)
//                     val isInside = isUserInsideGeofence(location, 26.2153, 84.3588, radius)
                    if (isInside) {


                        when {
                            isBoardingLoadingProvided.isNullOrEmpty() -> {
                                toastLong(getString(R.string.please_select_all_details_correct))
                            }

                            image1Base64.isNullOrEmpty() -> {
                                toastLong(getString(R.string.please_capture_image_from_your_phone))
                            }

                            else -> {
                                reverificationSettlement()
                            }
                        }

                    } else {
                        showAlertGeoFancingDialog(requireContext(),"Alert","❌ You are outside the institute area")

                    }
                } else {
                    toastLong("❌ Failed to retrieve current location")
                    showAlertGeoFancingDialog(requireContext(),"Alert","❌ Failed to retrieve current location Kindly on your gps from settings")
                }
            }



        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

            } else {
                Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show()
            }
        }

    private fun reverificationSettlement() {
        EditRemark = TvRemark.text.toString()

            // ✅ 1️⃣ Build REQUEST object
            val request = SettlementVeryficationUploadReq(
                candidateId,
                candidateName,
                instituteId,
                guardianName,
                settlementId,
                followUpId,
                ifscCode,
                loanAccountNo,
                EditRemark,
                image1Base64,
                latitude,
                longitude,
                batchId,
                bankName,
                cityName,
                creditFromBank,
                selfInvestment,
                totalInvestment,
                updatedBy,
                rollNo,
                salaryRange,
                employmentGiven,
                familyMemberPartTime,
                userPreferences.getUserName(),
                isBoardingLoadingProvided,
                BuildConfig.VERSION_NAME,

            )

            // ✅ 2️⃣ PRINT REQUEST JSON
            val gson = GsonBuilder().setPrettyPrinting().create()
            val requestJson = gson.toJson(request)

            Log.d("ReverificationAPI", "📤 REQUEST JSON:\n$requestJson")

            // ✅ 3️⃣ Call API
            commonViewModel.reverificationSettlementAPI(request)

            // ✅ 4️⃣ Collect RESPONSE
            lifecycleScope.launch {
                commonViewModel.reverificationSettlement.collectLatest { res ->
                    when (res) {

                        is Resource.Loading -> {
                            Log.d("ReverificationAPI", "⏳ Loading...")
                        }

                        is Resource.Success -> {

                            val apiResponse =
                                res.data as? SettlementVeryficationUploadInsertRes

                            val successMessage =
                                apiResponse?.responseMsg ?: "Success"

                            Toast.makeText(
                                requireContext(),
                                successMessage,   // 👈 "Verified successfully"
                                Toast.LENGTH_SHORT
                            ).show()
                            parentFragmentManager.setFragmentResult(
                                "REFRESH_DATA",
                                Bundle()
                            )
                             dismiss()


//                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }

                        is Resource.Error -> {
                            Log.e(
                                "ReverificationAPI",
                                "❌ ERROR: ${res.error?.message}"
                            )
                            Toast.makeText(
                                requireContext(),
                                res.error?.message ?: "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

    }


    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissions.any {
                ContextCompat.checkSelfPermission(
                    requireContext(), it
                ) != PackageManager.PERMISSION_GRANTED
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
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "Permissions granted!", Toast.LENGTH_SHORT)
                    .show()
            } else {
//                Toast.makeText(requireContext(), "Permissions denied!", Toast.LENGTH_SHORT)
//                    .show()
            }
        }
    }
    private fun openCamera(imageView: ImageView) {
        tvlattitude.text =""
        checkAndRequestPermissions()

        currentImageView = imageView

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Camera permission required!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)  // No extra output, no file
    }




    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val bitmap = result.data?.extras?.get("data") as? Bitmap

                if (bitmap == null || currentImageView == null) {
                    Toast.makeText(requireContext(), "Image capture failed!", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                @SuppressLint("MissingPermission")
                fun getCameralocation(context: Context, callback: (Location?) -> Unit) {

                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 1000
                    ).setMaxUpdates(1) // 👉 only 1 fresh update
                        .build()

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            val location = result.lastLocation
                            callback(location)

                            // 👉 stop updates (important)
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }

                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        callback(null)
                        return
                    }

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }


                getCameralocation(requireContext()) { location ->

                    if (location != null) {

                        val currentLat = location.latitude
                        val currentLng = location.longitude

                        val address = getAddressFromLatLng(currentLat, currentLng)

                        tvlattitude.text =
                            "Latitude: $currentLat\nLongitude: $currentLng"

                    } else {
                        tvlattitude.text = "Location not found"
                    }
                }
                val compressedBitmap = compressBitmap(bitmap)
                currentImageView?.setImageBitmap(compressedBitmap)

                val base64Image = bitmapToBase64(compressedBitmap)

                when (currentImageView?.id) {
                    R.id.image1 -> {
                        image1Base64 = base64Image
                    }
                }
            }
        }


    private fun getAddressFromLatLng(lat: Double, lng: Double) {

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)

            if (!addresses.isNullOrEmpty()) {

                address = addresses[0].getAddressLine(0)

                Log.d("ADDRESS", address ?: "No address")

                // ✅ UI update after address fetched
                tvlattitude.text =
                    "Latitude: $lat\nLongitude: $lng"
            }

        } catch (e: Exception) {
            e.printStackTrace()
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
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                outputStream
            ) // Increase quality to 90
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            Base64.encodeToString(byteArray, Base64.NO_WRAP) // Use NO_WRAP to avoid line breaks
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    fun showProgressBar() {
        // Ensure context is not null and the fragment is attached
        if (context != null && isAdded && progress?.isShowing == false) {
            progress?.show()
        }
    }

    fun hideProgressBar() {
        // Hide the progress bar if it's currently showing
        if (progress?.isShowing == true) {
            progress?.dismiss()
        }
    }
    fun showSnackBar(message: String) {
        val rootView = view ?: return   // Fragment root view

        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)

        snackBar.view.setPadding(0, 0, 0, 0)
        snackBar.view.elevation = 0f
        snackBar.view.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_rectangle_grey)

        snackBar.show()
    }



    private fun checkAndRequestStoragePermissions() {
        if (!hasStoragePermission(requireContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                storagePermissionLauncher.launch(AppUtil.storagePermissions)
            } else {
                storagePermissionLauncher.launch(arrayOf(AppUtil.legacyStoragePermission))
            }
        } else {
            // Permissions already granted, continue your logic
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
            // proceed with file/media access
        } else {
//            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    fun passbookCopybase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun base64ImageppointmentLetterbase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 👉 Parent Fragment ko notify karo ki BottomSheet close ho gaya
        parentFragmentManager.setFragmentResult("BOTTOM_SHEET_DISMISSED", Bundle())
        // Reset flag when bottomsheet closed

    }


    private fun showAlertGeoFancingDialog(context: Context, title: String, message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            findNavController().navigateUp()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)  // Prevent outside touch dismissal
        dialog.setCanceledOnTouchOutside(false) // Extra safety: disable outside clicks
        dialog.show()
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }





    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getCurrentLocation(onLocationResult: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showSnackBar("❌ Location permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            onLocationResult(location)
        }.addOnFailureListener {
            onLocationResult(null)
        }
    }
    private fun isUserInsideGeofence(currentLocation: Location, lat: Double, lng: Double, radius: Float): Boolean {
        val targetLocation = Location("").apply {
            latitude = lat
            longitude = lng
        }
        val distance = currentLocation.distanceTo(targetLocation)
        return distance <= radius
    }
    private fun checkCameraPermission(): Boolean {
        val permissionsNotGranted = java.util.ArrayList<String>()
        for (permission in neededPermissions) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNotGranted.add(permission)
            }
        }
        if (permissionsNotGranted.isNotEmpty()) {
            var shouldShowAlert = false
            for (permission in permissionsNotGranted) {
                shouldShowAlert =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
            }
            if (shouldShowAlert) {
                showPermissionAlert(permissionsNotGranted.toTypedArray())
            } else {
                requestPermissions(permissionsNotGranted.toTypedArray())
            }
            return false
        }
        return true
    }

    private fun showPermissionAlert(permissions: Array<String>) {
        val alertBuilder = AlertDialog.Builder(requireActivity())
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission Required")
        alertBuilder.setMessage("You must grant permission to access camera to run this application")
        alertBuilder.setPositiveButton(
            android.R.string.yes
        ) { _, _ -> requestPermissions(permissions) }
        val alert = alertBuilder.create()
        alert.show()
    }

    private fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            CAMERA_REQUEST
        )
    }


}







