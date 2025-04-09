package com.rsetiapp.common.fragments

import android.Manifest
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
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.karumi.dexter.BuildConfig
import com.rsetiapp.core.uidai.ekyc.UidaiKycRequest
import com.rsetiapp.core.uidai.ekyc.UidaiResp
import com.rsetiapp.core.uidai.ekyc.IntentModel
import com.rsetiapp.core.uidai.ekyc.IntentResponse
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.AttendanceCheckReq
import com.rsetiapp.common.model.request.AttendanceInsertReq
import com.rsetiapp.common.model.response.AttendanceData
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.common.model.response.VillageList
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.uidai.XstreamCommonMethods
import com.rsetiapp.core.uidai.XstreamCommonMethods.respDecodedXmlToPojoAuth
import com.rsetiapp.core.uidai.capture.CaptureResponse
import com.rsetiapp.core.util.AESCryptography
import com.rsetiapp.core.util.AppConstant
import com.rsetiapp.core.util.AppConstant.Constants.LANGUAGE
import com.rsetiapp.core.util.AppConstant.Constants.PRODUCTION
import com.rsetiapp.core.util.AppUtil.decodeBase64
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.log
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.FragmentVerifyUserAttendanceBinding
import com.rsetiapp.core.geoFancing.GeofenceHelper
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.copyToClipboard
import com.rsetiapp.core.util.gone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var selectedAttendanceTypeItem = ""
    private var batchId = ""
    private var aadhaarNo = ""
    private var candidateRollNo = ""
    private var checkIn = ""
    private var totalHours = ""
    private var checkOut = ""
    private var attendanceFlag = ""
    private var decryptedAadhaar = ""
    private lateinit var attendanceAdapter: ArrayAdapter<String>


    private val attendanceTypeList =
        listOf("Aadhaar Attendance","Offline Attendance")
    private var attendanceStatusRes: List<AttendanceData> = mutableListOf()

    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient

  /*  private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var radius: Float = 100f*/
    private var latitude = 28.6295826  // Example geofence latitude
    private var longitude = 77.2189311  // Example geofence longitude
    private var radius = 500f  // 100 meters radius

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startClock()

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
        commonViewModel.getAttendanceCheckStatus(AttendanceCheckReq(BuildConfig.VERSION_NAME,batchId,candidateId))
        collectAttendanceStatusResponse()
        checkAttendanceEligibility()


        attendanceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            attendanceTypeList
        )

        binding.spinnerAttendanceType.setAdapter(attendanceAdapter)

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



        geofenceHelper = GeofenceHelper(requireContext())

        requestLocationPermission()

        init()


        binding.spinnerAttendanceType.setOnItemClickListener { parent, view, position, id ->
            selectedAttendanceTypeItem = parent.getItemAtPosition(position).toString()
        }

    private fun init(){


        // Check if camera permission is granted

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
    private fun init() {
        checkCameraPermission()
        initEKYC()

        binding.btnCheckIn.setOnClickListener {


            if (selectedAttendanceTypeItem==""){

                toastShort("Kindly Select Attendance Mode First")

            }
            else{


                if (attendanceFlag=="checkin" && selectedAttendanceTypeItem=="Aadhaar Attendance"){

                    showProgressBar()
                    invokeCaptureIntent()

                }
                else{
                    // Offline Attendance

                    if (attendanceFlag=="checkin" && selectedAttendanceTypeItem=="Offline Attendance"){

                        //CheckOut Offline Attendance
                        toastShort("Offline Attendance marked checkout")

                    }
                    else
                        AppUtil.showAlertDialog(requireContext(),"Alert","Check In Attendance Already Marked")


                }
            }



        }

        binding.btnCheckOut.setOnClickListener {
            if (selectedAttendanceTypeItem==""){

                toastShort("Kindly Select Mode First")

            }

            else{

                if (attendanceFlag=="checkout" && selectedAttendanceTypeItem=="Aadhaar Attendance"){

                    showProgressBar()
                    invokeCaptureIntent()

                }
                else{

                    // Offline Attendance

                    if (attendanceFlag=="checkout" && selectedAttendanceTypeItem=="Offline Attendance"){

                      //CheckOut Offline Attendance
                        toastShort("Offline Attendance marked")

                    }
                    else
                        AppUtil.showAlertDialog(requireContext(),"Alert","Kindly Mark Attendance CheckIn First")

                }

            }

          //  invokeCaptureIntent()

        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofenceHelper.addGeofence(latitude, longitude, radius, "ATTENDANCE_ZONE")
        } else {
            requestLocationPermission()
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

        collectFaceAuthResponse()
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
        val secureRandom = SecureRandom()
        return secureRandom.nextInt(9999).toString()
    }


    private fun invokeCaptureIntent() {

        try {
            val intent1 = Intent(AppConstant.Constants.CAPTURE_INTENT)
            intent1.putExtra(
                AppConstant.Constants.CAPTURE_INTENT_REQUEST,
                createPidOptions(getTransactionID(), "auth")
            )
            startUidaiAuthResult.launch(intent1)

            // val packageName = "com.example.otherapp" // Replace with the target app's package name
            val intent =
                requireContext().packageManager.getLaunchIntentForPackage(AppConstant.Constants.CAPTURE_INTENT)
            intent?.putExtra(
                AppConstant.Constants.CAPTURE_INTENT_REQUEST,
                createPidOptions(getTransactionID(), "auth")
            )
            if (intent != null) {
                startActivity(intent)
            }
        } catch (exp: Exception) {
            log("EKYCDATA", exp.toString())
        }

    }

    private fun createPidOptions(txnId: String, purpose: String): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<PidOptions ver=\"1.0\" env=\"${PRODUCTION}\">\n" + "   <Opts fCount=\"\" fType=\"\" iCount=\"\" iType=\"\" pCount=\"\" pType=\"\" format=\"\" pidVer=\"2.0\" timeout=\"\" otp=\"\" wadh=\"${AppConstant.Constants.WADH_KEY}\" posh=\"\" />\n" + "   <CustOpts>\n" + "      <Param name=\"txnId\" value=\"${txnId}\"/>\n" + "      <Param name=\"purpose\" value=\"$purpose\"/>\n" + "      <Param name=\"language\" value=\"$LANGUAGE}\"/>\n" + "   </CustOpts>\n" + "</PidOptions>"
    }


    private fun handleCaptureResponse(captureResponse: String) {
        try {



            // Parse the capture response XML to an object
            val response = CaptureResponse.fromXML(captureResponse)

            if (response.isSuccess) {

                showProgressBar()
                // Process the response to generate the PoiType or other required fields
                val poiType = XstreamCommonMethods.processPidBlockEkyc(
                    response.toXML(),
                 // decryptedAadhaar
                //   "939625617876",
                    "877833331122",
                    false,
                    requireContext()
                )


                // Define Pre-Production URL (use a constant or environment configuration in production)
                //  val authURL = "http://10.247.252.95:8080/NicASAServer/ASAMain" //preProd
                val authURL = "http://10.247.252.93:8080/NicASAServer/ASAMain"  //Prod

                // Record the start time for elapsed time computation
                startTime = SystemClock.elapsedRealtime()

                // Post the processed data for Face Authentication
                commonViewModel.postOnAUAFaceAuthNREGA(
                    AppConstant.StaticURL.FACE_AUTH_UIADI,
                    UidaiKycRequest(poiType, authURL)
                )
                // Handle Aadhaar authentication or additional processing here if required
            } else {
                hideProgressBar()
                toastLong(getString(R.string.kyc_failed_msg))

            }


        } catch (e: SecurityException) {
            // Handle camera permission-related issues
            hideProgressBar()
            e.printStackTrace()
            toastShort("Camera permission is required for this feature.")
            log("EKYCDATA", "SecurityException: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // Handle cases where the response parsing might fail
            hideProgressBar()
            e.printStackTrace()
            toastShort("Invalid Capture Response format.")
            log("EKYCDATA", "IllegalArgumentException: ${e.message}")
        } catch (e: Exception) {
            // Catch all other exceptions
            hideProgressBar()
            e.printStackTrace()
            toastShort("An error occurred while processing the response.")
            log("EKYCDATA", "Exception: ${e.message}")
        //    e.message?.copyToClipboard(requireContext())
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
            try {
                collectLatestLifecycleFlow(commonViewModel.postOnAUAFaceAuthNREGA) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                        }

                        is Resource.Error -> {
                            hideProgressBar()
                            resource.error?.let { errorResponse ->
                                toastShort(errorResponse.message)
                                log("EKYCDATA", errorResponse.message ?: "Unknown error message")
                            } ?: run {
                                toastShort("Nothing to show pls try again")
                            }
                        }

                        is Resource.Success -> {

                            resource.data?.body()?.let { uidaiData: UidaiResp ->
                                try {
                                    val kycResp = XstreamCommonMethods.respDecodedXmlToPojoEkyc(
                                        uidaiData.PostOnAUA_Face_authResult
                                    )

                                    //  uidaiData.PostOnAUA_Face_authResult.copyToClipboard(requireContext())

                                    log("EKYCDATA", kycResp.toString())

                                    if (kycResp.isSuccess) {
                                        val bytes: ByteArray =
                                            Base64.decode(kycResp.uidData.pht, Base64.DEFAULT)
                                        val bitmap =
                                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                        userPhotoUIADI = bitmap
                                        ekycImage = kycResp.uidData.pht ?: ""

                                        log("EKYCDATA", userPhotoUIADI.toString())
                                        log("EKYCDATA", ekycImage)


                                        name = kycResp.uidData.poi.name ?: "N/A"
                                        photo = kycResp.uidData.pht ?: "N/A"
                                        gender = kycResp.uidData.poi.gender ?: "N/A"
                                        dob = kycResp.uidData.poi.dob ?: "N/A"
                                        careOf = kycResp.uidData.poa.co ?: "N/A"
                                        state = kycResp.uidData.poa.state ?: "N/A"
                                        dist = kycResp.uidData.poa.dist ?: "N/A"
                                        block = kycResp.uidData.poa.subdist ?: "N/A"
                                        village = kycResp.uidData.poa.vtc ?: "N/A"
                                        street = kycResp.uidData.poa.loc ?: "N/A"
                                        po = kycResp.uidData.poa.po ?: "N/A"
                                        pinCode = kycResp.uidData.poa.pc ?: "N/A"


                                        hideProgressBar()
                                        val currentDate = LocalDate.now()
                                        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                        val currentTime = LocalTime.now()
                                        val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mma"))
                                        val timeFormatter = DateTimeFormatter.ofPattern("hh:mma")



                                       /* if (checkIn){

                                        }*/

                                        if (attendanceFlag== "checkin"){


                                            commonViewModel.getInsertAttendance(AttendanceInsertReq(BuildConfig.VERSION_NAME,batchId,candidateId,formattedDate,"checkin",
                                                formattedTime,"","",candidateName))
                                        }
                                    else{

                                            val checkInTime = LocalTime.parse(checkIn, timeFormatter)
                                            val checkOutTime = LocalTime.parse(formattedTime, timeFormatter)
                                            val duration = Duration.between(checkInTime, checkOutTime)
                                            val hours = duration.toHours()
                                            val minutes = duration.toMinutes() % 60

                                            val totalHoursValue = String.format("%02d:%02d:00", hours, minutes) // Format as HH:mm:ss
                                            toastLong(totalHoursValue)

                                            commonViewModel.getInsertAttendance(AttendanceInsertReq(BuildConfig.VERSION_NAME,batchId,candidateId,formattedDate,"checkout",
                                                "",formattedTime,totalHoursValue,candidateName))
                                    }

                                        collectAttendanceInsertResponse()
                                    } else {
                                        hideProgressBar()
                                        val decodedRar = decodeBase64(kycResp.rar)
                                        decodedRar?.let { decodedRarParsed ->
                                            val authRes = respDecodedXmlToPojoAuth(decodedRarParsed)
                                            val errorDesc =
                                                XstreamCommonMethods.getAuthErrorDescription(authRes.info)
                                            toastShort(errorDesc)


                                        } ?: toastShort("Try Again")
                                    }
                                } catch (e: Exception) {
                                    hideProgressBar()
                                    e.printStackTrace()
                                    log("EKYCDATA", "Error processing KYC response: ${e.message}")
                                    toastShort("Try Again")
                                }
                            }
                                ?: toastShort(getString(R.string.something_went_wrong_at_uidai_site))
                        }

                        else -> {

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                hideProgressBar()
                log("EKYCDATA", "Unhandled error: ${e.message}")
                toastShort("Try Again")
            }
        }
    }


    private fun requestLocationPermission() {
        val locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (!granted) {
                val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                if (!showRationale) {
                    // Permission is permanently denied, direct to settings
                    AlertDialog.Builder(requireContext())
                        .setTitle("Permission Required")
                        .setMessage("Location permission is required to mark attendance. Please enable it in settings.")
                        .setPositiveButton("Go to Settings") { _, _ ->
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = android.net.Uri.fromParts("package", requireContext().packageName, null)
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Location permission required!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        locationPermission.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }


    private fun checkAttendanceEligibility() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                val distance = FloatArray(1)
                Location.distanceBetween(location.latitude, location.longitude, latitude, longitude, distance)

                if (distance[0] <= radius) {
                    hideProgressBar()


                } else {
               showAlertGeoFancingDialog(requireContext(),"Alert","Not in attendance zone!")
                }

                }
            else
            showAlertGeoFancingDialog(requireContext(),"Alert","Kindly Enable GPS")

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
                                attendanceStatusRes = getAttendanceCheckStatus.wrappedList

                                for (x in attendanceStatusRes) {

                                  checkIn = x.checkIn//00:00
                                  totalHours = x.totalHours//00:00:00
                               //     latitude = x.lattitude.toDouble()
                                   checkOut = x.checkOut
                                   // radius = x.radius.toFloat()
                                 attendanceFlag = x.attendanceFlag
                               //   longitude = x.longitude.toDouble()


                                    binding.tvCheckInValue.text= x.checkIn
                                    binding.tvCheckOutValue.text= x.checkOut
                                    binding.tvTotalHoursValue.text= x.totalHours

                                }


                            } else {
                                showSnackBar(getAttendanceCheckStatus.responseDesc)
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

                                userPhotoUIADI?.let { showBottomSheet(it,name,gender,dob,careOf) }

                            } else {
                                showSnackBar(getInsertAttendance.responseDesc)
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
        image: Bitmap,
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
}