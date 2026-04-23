package com.rsetiapp.common.fragments

import android.Manifest
import android.R.attr.bitmap
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.location.Location
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rsetiapp.BuildConfig
import com.rsetiapp.core.uidai.ekyc.UidaiKycRequest
import com.rsetiapp.core.uidai.ekyc.IntentModel
import com.rsetiapp.core.uidai.ekyc.IntentResponse
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.AttendanceCheckReq
import com.rsetiapp.common.model.request.AttendanceInsertReq
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.uidai.XstreamCommonMethods
import com.rsetiapp.core.uidai.capture.CaptureResponse
import com.rsetiapp.core.util.AESCryptography
import com.rsetiapp.core.util.AppConstant
import com.rsetiapp.core.util.AppConstant.Constants.LANGUAGE
import com.rsetiapp.core.util.AppConstant.Constants.PRODUCTION
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.log
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.FragmentVerifyUserAttendanceBinding
import com.rsetiapp.core.geoFancing.GeofenceHelper
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

const val CAMERA_REQUEST = 101
class AttendanceFragment : BaseFragment<FragmentVerifyUserAttendanceBinding>(
    FragmentVerifyUserAttendanceBinding::inflate) {


    private val commonViewModel : CommonViewModel by activityViewModels()
    private var name = ""
    private var dob = ""
    private var gender = ""
    private var careOf = ""
    private var state = ""
    private var dist = ""
    private var block = ""
    private var po = ""
    private var pinCode = ""
    private var street = ""
    private var village = ""
    private var photo = ""
    private var candidateId = ""
    private var candidateName = ""
    private var candidateMobile = ""
    private var candidateEmail = ""
    private var candidateGender = ""
    private var candidateDob = ""
    private var candidateDp = ""
    private var batchId = ""
    private var aadhaarNo = ""
    private var candidateRollNo = ""
    private var checkIn = ""
    private var totalHours = ""
    private var checkOut = ""
    private var attendanceFlag = ""
    private var decryptedAadhaar = ""


    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /*  private var latitude: Double = 0.0
      private var longitude: Double = 0.0
      var radius: Float = 100f*/
    private var latitude = 26.2153  // Example geofence latitude
    private var longitude = 84.3588  // Example geofence longitude
    private var radius = 50f  // 100 meters radius

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFaceAuthResponse()
        startClock()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        userPreferences = UserPreferences(requireContext())
        collectAttendanceStatusResponse()
        checkLocationPermission()


        binding.tvCurrentDate.text= AppUtil.getCurrentDateForAttendance()
        candidateId = arguments?.getString("candidateId").toString()
        candidateName = arguments?.getString("candidateName").toString()
        candidateMobile = arguments?.getString("candidateMobile").toString()
        candidateEmail = arguments?.getString("candidateEmail").toString()
        candidateGender = arguments?.getString("candidateGender").toString()
        candidateDob = arguments?.getString("candidateDob").toString()
        candidateDp = arguments?.getString("candidateDp").toString()
        batchId = arguments?.getString("batchId").toString()
        candidateRollNo = arguments?.getString("candidateRollNo").toString()
        aadhaarNo = arguments?.getString("aadhaarNo").toString()

        commonViewModel.getAttendanceCheckStatus(AppUtil.getSavedTokenPreference(requireContext()),AttendanceCheckReq(BuildConfig.VERSION_NAME,batchId,candidateId
            ,AppUtil.getAndroidId(requireContext()),userPreferences.getUseID()))




        decryptedAadhaar = AESCryptography.decryptIntoString(aadhaarNo,
            AppConstant.Constants.ENCRYPT_KEY,
            AppConstant.Constants.ENCRYPT_IV_KEY)

        binding.tvAadhaarName.text=candidateName
        binding.tvRollNoValue.text=candidateRollNo
        binding.tvEmailMobile.text=candidateEmail
        binding.tvAaadharMobile.text=candidateMobile
        binding.tvAaadharGender.text=candidateGender
        binding.tvAaadharDob.text=candidateDob

        loadBase64Image(candidateDp, binding.circleImageView)


        init()



    }
    private fun startClock() {
        lifecycleScope.launch {
            while (isAdded) { // Check if fragment is attached
                val currentTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
                binding.currentTime.text = currentTime
                delay(1000) // Update every second
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun init() {
        checkCameraPermission()
        initEKYC()

        binding.btnCheckIn.setOnClickListener {



            if (attendanceFlag=="checkin"){
                //for audit
                showProgressBar()
                invokeCaptureIntent()
                /*   val currentDate = LocalDate.now()
                   val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                   val currentTime = LocalTime.now()
                   val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))  // ✅ 24-hour format\
                   val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")


                   commonViewModel.getInsertAttendance(AppUtil.getSavedTokenPreference(requireContext()),AttendanceInsertReq(AppUtil.getAndroidId(requireContext()),userPreferences.getUseID(),
                       BuildConfig.VERSION_NAME,batchId,candidateId,
                       currentDate.toString(),"checkin",
                       formattedTime,"","",candidateName,AppUtil.getSavedEntityPreference(requireContext()),AppUtil.getSavedOrgIdPreference(requireContext()),AppUtil.getSavedHRIdPreference(requireContext())))
                       collectAttendanceInsertResponse()*/



            }
            else showSnackBar("Checkin Already marked")





        }

        binding.btnCheckOut.setOnClickListener {


            if (attendanceFlag=="checkout"){

                //for audit
                showProgressBar()
                invokeCaptureIntent()

                /* val currentDate = LocalDate.now()
                 val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                 val currentTime = LocalTime.now()

                 val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))  // ✅ 24-hour format\
                 val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")


                 val checkInTime = LocalTime.parse(checkIn, timeFormatter)
                 val checkOutTime = LocalTime.parse(formattedTime, timeFormatter)
                 val duration = Duration.between(checkInTime, checkOutTime)
                 val hours = duration.toHours()
                 val minutes = duration.toMinutes() % 60

                 val totalHoursValue = String.format("%02d:%02d:00", hours, minutes) // Format as HH:mm:ss

                 commonViewModel.getInsertAttendance(AppUtil.getSavedTokenPreference(requireContext()),AttendanceInsertReq(AppUtil.getAndroidId(requireContext()),userPreferences.getUseID(),
                     BuildConfig.VERSION_NAME,batchId,candidateId,
                     currentDate.toString(),"checkout",
                     "",formattedTime,totalHoursValue,candidateName,AppUtil.getSavedEntityPreference(requireContext()),AppUtil.getSavedOrgIdPreference(requireContext()),AppUtil.getSavedHRIdPreference(requireContext())))

                 collectAttendanceInsertResponse()*/


            }


            else showSnackBar("Kindly mark checkin First")




        }

    }
    private var intentResponse: IntentResponse? = null
    private val neededPermissions = arrayOf(Manifest.permission.CAMERA)
    private var startTime: Long = 0
    private var userPhotoUIADI: Bitmap? = null
    private var ekycImage: String = ""

    private fun initEKYC() {


        intentResponse = IntentResponse(
            kycStatus = false,
            faceAuthStatus = false,
            partialKycStatus = false,
            uidaiStatusCode = "",
            txnId = "",
            kycTimeStamp = "",
            faceAuthTimeStamp = "",
            partialKycTimeStamp = "",
            similarity = 0.0,
            BuildConfig.VERSION_NAME
        )
        var request = requireArguments().getString("pmayg_request")

        if (request == null) {
            request = requireActivity().intent.getStringExtra("request")
        }

        val intentModel = Gson().fromJson(
            request?.let {
                AESCryptography.decryptIntoString(
                    it,
                    AppConstant.Constants.CRYPT_ID,
                    AppConstant.Constants.CRYPT_IV
                )
            },
            IntentModel::class.java
        )

        // setConsentText()
    }

    private val startUidaiAuthResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    if (intent != null) {
                        val captureResponse =
                            intent.getStringExtra(AppConstant.Constants.CAPTURE_INTENT_RESPONSE_DATA)

                        if (!captureResponse.isNullOrEmpty()) {
                            handleCaptureResponse(captureResponse)
                        } else {
                            log("handleCaptureResponse", "Capture response data is null or empty.")
                            toastShort("Capture response is empty.")
                        }
                    } else {
                        log("handleCaptureResponse", "Intent data is null.")
                        toastShort("Failed to get capture response data.")
                    }
                } else {
                    toastLong("Failed to capture data.")
                    log("handleCaptureResponse", "Activity result code: ${result.resultCode}")
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                toastShort("Error: Missing data in result.")
                log("startUidaiAuthResult", "NullPointerException: ${e.message}")

            } catch (e: Exception) {
                e.printStackTrace()
                toastShort("An error occurred while processing the result.")
                log("startUidaiAuthResult", "Exception: ${e.message}")
            }
        }



    private fun getTransactionID(): String {
        val prefix = "RSETI"
        val suffix = "AEAD"

        // 12 digit random number
        val random = SecureRandom()
        val n = (100000000000L + (random.nextDouble() * 900000000000L)).toLong()

        val date = Date()

        val yyyy = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
        val mm = SimpleDateFormat("MM", Locale.getDefault()).format(date)
        val dd = SimpleDateFormat("dd", Locale.getDefault()).format(date)

        val hh = SimpleDateFormat("HH", Locale.getDefault()).format(date) // 24-hour format better
        val min = SimpleDateFormat("mm", Locale.getDefault()).format(date)
        val ss = SimpleDateFormat("ss", Locale.getDefault()).format(date)

        val strDate = yyyy + mm + dd
        val strTime = hh + min + ss

        return "$prefix$n$strDate$strTime$suffix"
    }



    private fun invokeCaptureIntent() {
        try {
            val intent = Intent(AppConstant.Constants.CAPTURE_INTENT)
            intent.putExtra(
                AppConstant.Constants.CAPTURE_INTENT_REQUEST,
                createPidOptions(getTransactionID(), "auth")
            )

            startUidaiAuthResult.launch(intent) //  only one call

        } catch (exp: Exception) {
            log("EKYCDATA", exp.toString())
            hideProgressBar()
            toastShort("Failed to open capture app")
        }
    }


    private fun createPidOptions(txnId: String, purpose: String): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<PidOptions ver=\"1.0\" env=\"${PRODUCTION}\">\n" + "   <Opts fCount=\"\" fType=\"\" iCount=\"\" iType=\"\" pCount=\"\" pType=\"\" format=\"\" pidVer=\"2.0\" timeout=\"\" otp=\"\" wadh=\"${AppConstant.Constants.WADH_KEY}\" posh=\"\" />\n" + "   <CustOpts>\n" + "      <Param name=\"txnId\" value=\"${txnId}\"/>\n" + "      <Param name=\"purpose\" value=\"$purpose\"/>\n" + "      <Param name=\"language\" value=\"$LANGUAGE}\"/>\n" + "   </CustOpts>\n" + "</PidOptions>"
    }

    private fun handleCaptureResponse(captureResponse: String) {
        try {
            val response = CaptureResponse.fromXML(captureResponse)

            if (response.isSuccess) {
                showProgressBar()

                val poiType = XstreamCommonMethods.processPidBlockEkyc(
                    response.toXML(),
                    decryptedAadhaar,
                    false,
                    requireContext()
                )

                val authURL = "http://10.247.252.93:8080/NicASAServer/ASAMain"

                commonViewModel.postOnAUAFaceAuthNREGA(
                    AppConstant.StaticURL.FACE_AUTH_UIADI,
                    UidaiKycRequest(poiType, authURL)
                )

            } else {
                hideProgressBar()
                toastLong("KYC Failed")
            }

        } catch (e: Exception) {
            hideProgressBar()
            e.printStackTrace()
            toastShort("Error processing capture response")
        }
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

    private fun collectFaceAuthResponse() {
        lifecycleScope.launch {

            collectLatestLifecycleFlow(commonViewModel.postOnAUAFaceAuthNREGA) { resource ->

                when (resource) {

                    is Resource.Loading -> {
                        showProgressBar()
                    }

                    is Resource.Error -> {
                        hideProgressBar()
                        toastShort(resource.error?.message ?: "API Error")
                        log("API_ERROR", resource.error?.message ?: "")
                    }

                    is Resource.Success -> {
                        hideProgressBar()

                        resource.data?.body()?.let { uidaiData ->

                            try {
                                val kycResp =
                                    XstreamCommonMethods.respDecodedXmlToPojoEkyc(
                                        uidaiData.PostOnAUA_Face_authResult
                                    )

                                if (kycResp.isSuccess) {

                                    val bytes: ByteArray =
                                        Base64.decode(kycResp.uidData.pht, Base64.DEFAULT)
                                    val bitmap =
                                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                    userPhotoUIADI = bitmap
                                    ekycImage = kycResp.uidData.pht ?: ""
                                    name = kycResp.uidData.poi.name ?: "N/A"
                                    photo = kycResp.uidData.pht ?: "N/A"
                                    gender = kycResp.uidData.poi.gender ?: "N/A"
                                    dob = kycResp.uidData.poi.dob ?: "N/A"
                                    careOf = kycResp.uidData.poa.co ?: "N/A"



                                    val currentDate = LocalDate.now()
                                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                                    val currentTime = LocalTime.now()
                                    val formattedTime = currentTime.format(
                                        DateTimeFormatter.ofPattern("HH:mm:ss")
                                    )

                                    val timeFormatter =
                                        DateTimeFormatter.ofPattern("HH:mm:ss")

                                    if (attendanceFlag == "checkin") {

                                        commonViewModel.getInsertAttendance(
                                            AppUtil.getSavedTokenPreference(requireContext()),
                                            AttendanceInsertReq(
                                                AppUtil.getAndroidId(requireContext()),
                                                userPreferences.getUseID(),
                                                BuildConfig.VERSION_NAME,
                                                batchId,
                                                candidateId,
                                                currentDate,
                                                "checkin",
                                                formattedTime,
                                                "",
                                                "",
                                                candidateName,
                                                AppUtil.getSavedEntityPreference(requireContext()),
                                                AppUtil.getSavedOrgIdPreference(requireContext()),
                                                AppUtil.getSavedHRIdPreference(requireContext())
                                            )
                                        )

                                    } else {

                                        val checkInTime =
                                            LocalTime.parse(checkIn, timeFormatter)

                                        val checkOutTime =
                                            LocalTime.parse(formattedTime, timeFormatter)

                                        val duration =
                                            Duration.between(checkInTime, checkOutTime)

                                        val totalHoursValue = String.format(
                                            "%02d:%02d:%02d",
                                            duration.toHours(),
                                            duration.toMinutes() % 60,
                                            duration.seconds % 60
                                        )

                                        commonViewModel.getInsertAttendance(
                                            AppUtil.getSavedTokenPreference(requireContext()),
                                            AttendanceInsertReq(
                                                AppUtil.getAndroidId(requireContext()),
                                                userPreferences.getUseID(),
                                                BuildConfig.VERSION_NAME,
                                                batchId,
                                                candidateId,
                                                currentDate,
                                                "checkout",
                                                "",
                                                formattedTime,
                                                totalHoursValue,
                                                candidateName,
                                                AppUtil.getSavedEntityPreference(requireContext()),
                                                AppUtil.getSavedOrgIdPreference(requireContext()),
                                                AppUtil.getSavedHRIdPreference(requireContext())
                                            )
                                        )
                                    }

                                    collectAttendanceInsertResponse()

                                } else {
                                    toastShort("Face Auth Failed")
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                toastShort("Parsing Error")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadBase64Image(base64String: String?, imageView: ImageView) {
        if (base64String.isNullOrEmpty()) {
            return  // Avoid processing if the string is null or empty
        }

        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            // Set bitmap to ImageView
            imageView.setImageBitmap(bitmap)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun collectAttendanceStatusResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getAttendanceCheckStatus) { result ->
                when (result) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(result.error?.message ?: "Error fetching data")
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        result.data?.let { getAttendanceCheckStatus ->
                            if (getAttendanceCheckStatus.responseCode == 200) {
                                val  attendanceStatusRes = getAttendanceCheckStatus.wrappedList

                                for (x in attendanceStatusRes) {

                                    checkIn = x.checkIn//00:00
                                    totalHours = x.totalHours//00:00:00
                                    latitude = x.lattitude.toDouble()
                                    checkOut = x.checkOut
                                    radius = x.radius.toFloat()
                                    attendanceFlag = x.attendanceFlag
                                    longitude = x.longitude.toDouble()


                                    getCurrentLocation { location ->
                                        if (location != null) {
                                            val isInside = isUserInsideGeofence(location, latitude, longitude, radius)
                                           //  val isInside = isUserInsideGeofence(location, 26.2153, 84.3588, 5000000f)
                                            if (isInside) {


                                                //    findNavController().navigate(SdrListFragmentDirections.actionSdrListFragmentToSdrVisitReport(formName,instituteName,finYear,instituteId))
                                            } else {
                                                showAlertGeoFancingDialog(requireContext(),"Alert","❌ You are outside the institute area")

                                            }
                                        } else {
                                            toastLong("❌ Failed to retrieve current location")
                                            showAlertGeoFancingDialog(requireContext(),"Alert","❌ Failed to retrieve current location Kindly on your gps from settings")
                                        }
                                    }

                                    binding.tvCheckInValue.text= x.checkIn
                                    binding.tvCheckOutValue.text= x.checkOut
                                    binding.tvTotalHoursValue.text= x.totalHours

                                }


                            }   else if (getAttendanceCheckStatus.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getAttendanceCheckStatus.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> {

                        showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun collectAttendanceInsertResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getInsertAttendance) { result ->
                when (result) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(result.error?.message ?: "Error fetching data")
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        result.data?.let { getInsertAttendance ->
                            if (getInsertAttendance.responseCode == 200) {

                                showSnackBar(getInsertAttendance.responseDesc)
                                toastLong("Attendance Marked")


                                showBottomSheet(userPhotoUIADI,name,gender,dob,careOf)

                            }
                            else if (getInsertAttendance.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getInsertAttendance.responseDesc)
                            }
                        }
                    }

                    else -> {

                        showSnackBar("Internal Server Error")
                    }
                }
            }
        }
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

    @SuppressLint("SuspiciousIndentation")
    private fun showBottomSheet(
        image: Bitmap?,
        name: String,
        gender: String,
        dateOfBirth: String,
        careOf: String
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        // Inflate the layout
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)

        // Prevent closing when tapping outside
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        // Find views
        val imageView = view.findViewById<ImageView>(R.id.circleImageView)
        val nameView = view.findViewById<TextView>(R.id.attendancCandidateName)
        val genderView = view.findViewById<TextView>(R.id.attendancGender)
        val dobView = view.findViewById<TextView>(R.id.attendancCDob)
        val careOfView = view.findViewById<TextView>(R.id.attendancCareOf)
        val okButton = view.findViewById<TextView>(R.id.tvLogin)

        // Set data
        imageView.setImageBitmap(image)
        nameView.text = name
        genderView.text = gender
        dobView.text = dateOfBirth
        careOfView.text = careOf

        // Handle OK button click
        okButton.setOnClickListener {

            bottomSheetDialog.dismiss()
            findNavController().navigateUp()
        }

        // Handle back button press
        bottomSheetDialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Show a confirmation dialog before closing
                AlertDialog.Builder(requireContext())
                    .setTitle("Exit")
                    .setMessage("Do you want to close this screen?")
                    .setPositiveButton("Yes") { _, _ ->
                        bottomSheetDialog.dismiss()
                    }
                    .setNegativeButton("No", null)
                    .show()
                return@setOnKeyListener true
            }
            false
        }

        // Show the BottomSheetDialog
        bottomSheetDialog.show()
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

    private fun getCurrentLocation(onLocationResult: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toastLong("❌ Location permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            onLocationResult(location)
        }.addOnFailureListener {
            onLocationResult(null)
        }
    }

    private fun isUserInsideGeofence(
        currentLocation: Location,
        lat: Double,
        lng: Double,
        radius: Float
    ): Boolean {
        val targetLocation = Location("").apply {
            latitude = lat
            longitude = lng
        }
        val distance = currentLocation.distanceTo(targetLocation)
        return distance <= radius
    }
}