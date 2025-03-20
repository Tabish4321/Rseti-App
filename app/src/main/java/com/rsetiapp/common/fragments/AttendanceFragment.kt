package com.rsetiapp.common.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.rsetiapp.core.uidai.ekyc.UidaiKycRequest
import com.rsetiapp.core.uidai.ekyc.UidaiResp
import com.rsetiapp.core.uidai.ekyc.IntentModel
import com.rsetiapp.core.uidai.ekyc.IntentResponse
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
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
import com.rsetiapp.core.util.copyToClipboard
import com.rsetiapp.core.util.log
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.FragmentVerifyUserAttendanceBinding
import kotlinx.coroutines.launch
import java.security.SecureRandom

const val CAMERA_REQUEST = 101
class AttendanceFragment : BaseFragment<FragmentVerifyUserAttendanceBinding>(
    FragmentVerifyUserAttendanceBinding::inflate) {

    private val commonViewModel : CommonViewModel by activityViewModels()

    private var selectedState = ""
    private var showPassword = true
    private var aadhaarValidate = false
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
    private var tokenGen = ""
    private var tokenViaCreate = ""
    private var village = ""
    private var photo = ""
    private var selectedStateCode = ""
    private var selectedStateLgdCode = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){

        // Check if camera permission is granted
        checkCameraPermission()
        initEKYC()
        binding.tvMarkAttendance.setOnClickListener {
            invokeCaptureIntent()
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
                            log("handleCaptureResponse", captureResponse)
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


    // private fun getTransactionID() = Random(System.currentTimeMillis()).nextInt(9999).toString()

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

            captureResponse.copyToClipboard(requireContext())

            // Parse the capture response XML to an object
            val response = CaptureResponse.fromXML(captureResponse)

            if (response.isSuccess) {
                showProgressBar()
                // Process the response to generate the PoiType or other required fields
                val poiType = XstreamCommonMethods.processPidBlockEkyc(
                    response.toXML(),
                    binding.tvMarkAttendance.text.toString().trim(),
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
            e.message?.copyToClipboard(requireContext())
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


                                        // Get the last 4 digits
                                        val input = binding.tvMarkAttendance.text.toString()
                                        val lastFourDigits = input.takeLast(4)
                                        // Create the masked string
                                        val maskedString =
                                            "*".repeat(input.length - 4) + lastFourDigits

                                        hideProgressBar()
                                        toastLong("name : $name,  gender:$gender, dob:$dob, careOf:$careOf")

                                        // toastShort("Ekyc Completed")
                                    } else {
                                        hideProgressBar()
                                        val decodedRar = decodeBase64(kycResp.rar)
                                        decodedRar?.let { decodedRarParsed ->
                                            val authRes = respDecodedXmlToPojoAuth(decodedRarParsed)
                                            val errorDesc =
                                                XstreamCommonMethods.getAuthErrorDescription(authRes.info)
                                            log("EKYCDATA", errorDesc)

                                            toastShort("EKYCDATA: Failed")
                                        } ?: toastShort("Getting Error")
                                    }
                                } catch (e: Exception) {
                                    hideProgressBar()
                                    e.printStackTrace()
                                    log("EKYCDATA", "Error processing KYC response: ${e.message}")
                                    toastShort("Error processing KYC response")
                                }
                            }
                                ?: toastShort(getString(R.string.something_went_wrong_at_uidai_site))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                hideProgressBar()
                log("EKYCDATA", "Unhandled error: ${e.message}")
                toastShort("An unexpected error occurred. Please try again.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}