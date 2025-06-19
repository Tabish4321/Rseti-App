package com.rsetiapp.common.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.os.Looper
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.InsertSdrVisitReq
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.gone
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.visible
import com.rsetiapp.databinding.FragmentSdrVisitReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale


@AndroidEntryPoint
class SdrVisitReport : BaseFragment<FragmentSdrVisitReportBinding> (FragmentSdrVisitReportBinding::inflate) {

    private val cleanlinessList = listOf("Good", "Satisfactory", "Needs Improvement")
    private val yesNoList = listOf("Yes", "No")
    private var currentImageView: ImageView? = null

    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var cleanlinessAdapter: ArrayAdapter<String>
    private var selectedCleanlinessItem = ""
    private var cleanlinessImage = ""
    private var cleanlinessImageLat : Double? = null
    private var cleanlinessImageLang : Double? = null


    private lateinit var signBoardAdapter: ArrayAdapter<String>
    private var selectedSignBoardItem = ""
    private var signBoardImage = ""
    private var signBoardImageLat : Double? = null
    private var signBoardImageLang : Double? = null


    private lateinit var timeTableDrawnAdapter: ArrayAdapter<String>
    private var selectedTimeTableDrawnItem = ""
    private var timeTableDrawnImage = ""
    private var timeTableDrawnImageLat : Double? = null
    private var timeTableDrawnImageLang : Double? = null



    private lateinit var timeTableOnGoingAdapter: ArrayAdapter<String>
    private var selectedTimeTableOnGoingItem = ""
    private var timeTableOnGoingImage = ""
    private var timeTableOnGoingImageLat : Double? = null
    private var timeTableOnGoingImageLang : Double? = null



    private lateinit var actionPhotoAdapter: ArrayAdapter<String>
    private var selectedActionPhotoItem = ""
    private var actionPhotoImage = ""
    private var actionPhotoImageLat : Double? = null
    private var actionPhotoImageLang : Double? = null


    private lateinit var successActionPhotoAdapter: ArrayAdapter<String>
    private var selectedSuccessActionPhotoItem = ""
    private var successActionPhotoImage = ""
    private var successActionPhotoImageLat : Double? = null
    private var successActionPhotoImageLang : Double? = null


    private lateinit var officeTimingAdapter: ArrayAdapter<String>
    private var selectedOfficeTimingItem = ""
    private var officeTimingImage = ""
    private var officeTimingImageLat : Double? = null
    private var officeTimingImageLang: Double? = null


    private lateinit var trainingKitAdapter: ArrayAdapter<String>
    private var selectedTrainingKitItem = ""
    private var TrainingKitImage = ""
    private var TrainingKitImageLat : Double? = null
    private var TrainingKitImageLang: Double? = null

    private lateinit var edpLocalLanguageAdapter: ArrayAdapter<String>
    private var selectedEdpLocalLanguageItem = ""
    private var edpLocalLanguageImage = ""
    private var edpLocalLanguageImageLat : Double? = null
    private var edpLocalLanguageImageLang: Double? = null


    private lateinit var foodLocalLanguageAdapter: ArrayAdapter<String>
    private var selectedFoodLocalLanguageItem = ""
    private var foodLocalLanguageImage = ""
    private var instituteId = ""
    private var instituteName = ""
    private var finYear = ""
    private var monthCode = ""
    private var foodLocalLanguageImageLat : Double? = null
    private var foodLocalLanguageImageLang : Double? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        userPreferences = UserPreferences(requireContext())


        init()
    }

    private fun init(){
        listener()
    }

    private fun listener(){

        val formName = arguments?.getString("formName").toString()
         instituteId = arguments?.getString("rsetiInstituteId").toString()
         instituteName = arguments?.getString("rsetiInstituteName").toString()
         finYear = arguments?.getString("finYear").toString()
         monthCode = arguments?.getString("monthCode").toString()


        binding.tvFinancialYear.text= finYear
        binding.tvRsetiName.text= instituteName


        binding.tvFormName.text= formName
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.btnSubmit.setOnClickListener {




                val missingFields = mutableListOf<String>()

                if (selectedCleanlinessItem.isBlank() || cleanlinessImage.isBlank()) {
                    missingFields.add("Cleanliness")
                }
                if (selectedSignBoardItem.isBlank() || signBoardImage.isBlank()) {
                    missingFields.add("Sign Board")
                }
                if (selectedTimeTableDrawnItem.isBlank() || timeTableDrawnImage.isBlank()) {
                    missingFields.add("Time Table Drawn")
                }
                if (selectedTimeTableOnGoingItem.isBlank() || timeTableOnGoingImage.isBlank()) {
                    missingFields.add("Time Table On-going")
                }
                if (selectedActionPhotoItem.isBlank() || actionPhotoImage.isBlank()) {
                    missingFields.add("Action Photo")
                }
                if (selectedSuccessActionPhotoItem.isBlank() || successActionPhotoImage.isBlank()) {
                    missingFields.add("Success Action Photo")
                }
                if (selectedOfficeTimingItem.isBlank() || officeTimingImage.isBlank()) {
                    missingFields.add("Office Timing")
                }
                if (selectedTrainingKitItem.isBlank() || TrainingKitImage.isBlank()) {
                    missingFields.add("Training Kit")
                }
                if (selectedEdpLocalLanguageItem.isBlank() || edpLocalLanguageImage.isBlank()) {
                    missingFields.add("EDP Local Language")
                }
                if (selectedFoodLocalLanguageItem.isBlank() || foodLocalLanguageImage.isBlank()) {
                    missingFields.add("Food Local Language")
                }

                if (missingFields.isNotEmpty()) {
                    val message = "Please fill in or upload: ${missingFields.joinToString(", ")}"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                } else {


                    commonViewModel.insertSdrApi(AppUtil.getSavedTokenPreference(requireContext()),
                        InsertSdrVisitReq(BuildConfig.VERSION_NAME,userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()),instituteId.toInt(),finYear,monthCode.toInt()
                            ,selectedCleanlinessItem,
                            cleanlinessImage,
                            cleanlinessImageLat.toString(),
                            cleanlinessImageLang.toString(),selectedSignBoardItem,signBoardImage,
                            signBoardImageLat.toString(),
                            signBoardImageLang.toString(),selectedTimeTableDrawnItem,
                            timeTableDrawnImage,
                            timeTableDrawnImageLat.toString(),
                            timeTableDrawnImageLang.toString(),selectedTimeTableOnGoingItem,timeTableOnGoingImage,
                            timeTableOnGoingImageLat.toString(),
                            timeTableOnGoingImageLang.toString(),
                            selectedActionPhotoItem,actionPhotoImage,
                            actionPhotoImageLat.toString(),
                            actionPhotoImageLang.toString(),selectedSuccessActionPhotoItem,successActionPhotoImage,
                            successActionPhotoImageLat.toString(),
                            successActionPhotoImageLang.toString(),selectedOfficeTimingItem,
                            officeTimingImage,
                            officeTimingImageLat.toString(),
                            officeTimingImageLang.toString(),selectedTrainingKitItem,TrainingKitImage,
                            TrainingKitImageLat.toString(),
                            TrainingKitImageLang.toString(),selectedEdpLocalLanguageItem,edpLocalLanguageImage,
                            edpLocalLanguageImageLat.toString(),
                            edpLocalLanguageImageLang.toString(),selectedFoodLocalLanguageItem,foodLocalLanguageImage,
                            foodLocalLanguageImageLat.toString(),
                            foodLocalLanguageImageLang.toString()
                        )
                    )
                    collectSdrInsertResponse()
                }
            }

        binding.tvNameOfDirector.text= userPreferences.getUserName()




        //cleanlinessAdapter setting

        cleanlinessAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            cleanlinessList
        )

        binding.spinnerCleanliness.setAdapter(cleanlinessAdapter)



        binding.spinnerCleanliness.setOnItemClickListener { parent, view, position, id ->
            selectedCleanlinessItem = parent.getItemAtPosition(position).toString()

            binding.imageCleaniness.visible()
            binding.tvLocClean.visible()


        }


        //signBoardAdapter setting

        signBoardAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerSignboard.setAdapter(signBoardAdapter)



        binding.spinnerSignboard.setOnItemClickListener { parent, view, position, id ->
            selectedSignBoardItem = parent.getItemAtPosition(position).toString()


            binding.imageSignboard.visible()
            binding.tvLocSignBoard.visible()

        }



        //timeTableDrawnAdapter setting

        timeTableDrawnAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerTimetableCourse.setAdapter(timeTableDrawnAdapter)


        binding.spinnerTimetableCourse.setOnItemClickListener { parent, view, position, id ->
            selectedTimeTableDrawnItem = parent.getItemAtPosition(position).toString()

            binding.imageTimetableCourse.visible()
            binding.tvLocTimeTableCourse.visible()



        }


        //timeTableOnGoingAdapter setting

        timeTableOnGoingAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerTimetableOnGoingCourse.setAdapter(timeTableOnGoingAdapter)


        binding.spinnerTimetableOnGoingCourse.setOnItemClickListener { parent, view, position, id ->
            selectedTimeTableOnGoingItem = parent.getItemAtPosition(position).toString()


            binding.imageTimetableOnGoingCourse.visible()
            binding.tvLocTimeTableOnGoingCourse.visible()


        }





        //actionPhotoAdapter setting

        actionPhotoAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerActionPhotoDisplayed.setAdapter(actionPhotoAdapter)




        binding.spinnerActionPhotoDisplayed.setOnItemClickListener { parent, view, position, id ->
            selectedActionPhotoItem = parent.getItemAtPosition(position).toString()



            binding.imageActionPhotoDisplayed.visible()
            binding.tvLocActionPhotoDisplayed.visible()


        }


        //successActionPhotoAdapter setting

        successActionPhotoAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerActionPhotoDisplayedLocalLanguage.setAdapter(successActionPhotoAdapter)



        binding.spinnerActionPhotoDisplayedLocalLanguage.setOnItemClickListener { parent, view, position, id ->
            selectedSuccessActionPhotoItem = parent.getItemAtPosition(position).toString()


            binding.imageActionPhotoDisplayedLocalLanguage.visible()
            binding.tvLocActionPhotoDisplayedLocalLanguage.visible()

        }


        //officeTimingAdapter setting

        officeTimingAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerOfficeTimmingIsDisplayed.setAdapter(officeTimingAdapter)



        binding.spinnerOfficeTimmingIsDisplayed.setOnItemClickListener { parent, view, position, id ->
            selectedOfficeTimingItem = parent.getItemAtPosition(position).toString()


            binding.imageOfficeTimmingIsDisplayed.visible()
            binding.tvLocImageOfficeTimingIsDisplayed.visible()
        }

        //trainingKitAdapter setting

        trainingKitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerTrainingKitAvailable.setAdapter(trainingKitAdapter)



        binding.spinnerTrainingKitAvailable.setOnItemClickListener { parent, view, position, id ->
            selectedTrainingKitItem = parent.getItemAtPosition(position).toString()


            binding.imageTrainingKitAvailable.visible()
            binding.tvLocTrainingKitAvailable.visible()

        }


        //edpLocalLanguageAdapter setting

        edpLocalLanguageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerEdpInLocalLanguage.setAdapter(edpLocalLanguageAdapter)



        binding.spinnerEdpInLocalLanguage.setOnItemClickListener { parent, view, position, id ->
            selectedEdpLocalLanguageItem = parent.getItemAtPosition(position).toString()

            binding.imageEdpInLocalLanguage.visible()
            binding.tvLocEdpInLocalLanguage.visible()


        }


        //foodLocalLanguageAdapter setting

        foodLocalLanguageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yesNoList
        )

        binding.spinnerFoodDetailsInLocalLanguage.setAdapter(foodLocalLanguageAdapter)




        binding.spinnerFoodDetailsInLocalLanguage.setOnItemClickListener { parent, view, position, id ->
            selectedFoodLocalLanguageItem = parent.getItemAtPosition(position).toString()
            binding.imageFoodDetailsInLocalLanguage.visible()
            binding.tvLocFoodDetailsInLocalLanguage.visible()

        }


        binding.imageCleaniness.setOnClickListener {
            openCamera(binding.imageCleaniness)
            getCurrentLocation(
                addressTextView = binding.tvLocClean,
                setLat = { cleanlinessImageLat = it },
                setLng = { cleanlinessImageLang = it }
            )
        }

        binding.imageSignboard.setOnClickListener {
            openCamera(binding.imageSignboard)
            getCurrentLocation(
                addressTextView = binding.tvLocSignBoard,
                setLat = { signBoardImageLat = it },
                setLng = { signBoardImageLang = it }
            )
        }

        binding.imageTimetableCourse.setOnClickListener {
            openCamera(binding.imageTimetableCourse)
            getCurrentLocation(
                addressTextView = binding.tvLocTimeTableCourse,
                setLat = { timeTableDrawnImageLat = it },
                setLng = { timeTableDrawnImageLang = it }
            )
        }

        binding.imageTimetableOnGoingCourse.setOnClickListener {
            openCamera(binding.imageTimetableOnGoingCourse)
            getCurrentLocation(
                addressTextView = binding.tvLocTimeTableOnGoingCourse,
                setLat = { timeTableOnGoingImageLat= it },
                setLng = { timeTableOnGoingImageLang = it }
            )
        }

        binding.imageActionPhotoDisplayed.setOnClickListener {
            openCamera(binding.imageActionPhotoDisplayed)
            getCurrentLocation(
                addressTextView = binding.tvLocActionPhotoDisplayed,
                setLat = { actionPhotoImageLat= it },
                setLng = { actionPhotoImageLang = it }
            )
        }

        binding.imageActionPhotoDisplayedLocalLanguage.setOnClickListener {
            openCamera(binding.imageActionPhotoDisplayedLocalLanguage)
            getCurrentLocation(
                addressTextView = binding.tvLocActionPhotoDisplayedLocalLanguage,
                setLat = { successActionPhotoImageLat= it },
                setLng = { successActionPhotoImageLang = it }
            )
        }


        binding.imageOfficeTimmingIsDisplayed.setOnClickListener {
            openCamera(binding.imageOfficeTimmingIsDisplayed)
            getCurrentLocation(
                addressTextView = binding.tvLocImageOfficeTimingIsDisplayed,
                setLat = { officeTimingImageLat= it },
                setLng = { officeTimingImageLang = it }
            )
        }

        binding.imageTrainingKitAvailable.setOnClickListener {
            openCamera(binding.imageTrainingKitAvailable)
            getCurrentLocation(
                addressTextView = binding.tvLocTrainingKitAvailable,
                setLat = { TrainingKitImageLat= it },
                setLng = { TrainingKitImageLang = it }
            )
        }


        binding.imageEdpInLocalLanguage.setOnClickListener {
            openCamera(binding.imageEdpInLocalLanguage)
            getCurrentLocation(
                addressTextView = binding.tvLocEdpInLocalLanguage,
                setLat = { edpLocalLanguageImageLat= it },
                setLng = { edpLocalLanguageImageLang = it }
            )
        }


        binding.imageFoodDetailsInLocalLanguage.setOnClickListener {
            openCamera(binding.imageFoodDetailsInLocalLanguage)
            getCurrentLocation(
                addressTextView = binding.tvLocFoodDetailsInLocalLanguage,
                setLat = { foodLocalLanguageImageLat= it },
                setLng = { foodLocalLanguageImageLang = it }
            )
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
                Toast.makeText(requireContext(), "Permissions denied!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun openCamera(imageView: ImageView) {
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


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            if (bitmap == null || currentImageView == null) {
                Toast.makeText(requireContext(), "Image capture failed!", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val compressedBitmap = compressBitmap(bitmap)
            currentImageView?.setImageBitmap(compressedBitmap)

            val base64Image = bitmapToBase64(compressedBitmap)

            when (currentImageView?.id) {
                R.id.imageCleaniness -> {
                    cleanlinessImage = base64Image

                    Log.d("", "cleanlinessImage: $cleanlinessImage ")
                }

                R.id.imageSignboard -> {
                    signBoardImage = base64Image
                }

                R.id.imageTimetableCourse -> {
                    timeTableDrawnImage = base64Image
                }

                R.id.imageTimetableOnGoingCourse -> {
                    timeTableOnGoingImage = base64Image
                }
                R.id.imageActionPhotoDisplayed -> {
                    actionPhotoImage = base64Image
                }
                R.id.imageActionPhotoDisplayedLocalLanguage -> {
                    successActionPhotoImage = base64Image
                }

                R.id.imageOfficeTimmingIsDisplayed -> {
                    officeTimingImage = base64Image
                }
                R.id.imageEdpInLocalLanguage -> {
                    edpLocalLanguageImage = base64Image
                }

                R.id.imageTrainingKitAvailable -> {
                    TrainingKitImage  = base64Image
                }

                R.id.imageFoodDetailsInLocalLanguage -> {
                    foodLocalLanguageImage = base64Image
                }


            }

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



    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(
        addressTextView: TextView,
        setLat: (Double) -> Unit,
        setLng: (Double) -> Unit
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude

                setLat(lat)
                setLng(lng)

              //  Log.d("lat Lang", "getCurrentLocation: Lat: $lat, Lng: $lng")
                addressTextView.text = "Lat: $lat, Lng: $lng"

                getAddressFromLocation(lat, lng, addressTextView)
            } else {
                requestNewLocation(addressTextView, setLat, setLng)
            }
        }.addOnFailureListener {
            addressTextView.text = getString(R.string.location_not_found)
            Log.e("LocationError", "Failed to get location: ${it.message}")
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocation(
        addressTextView: TextView,
        setLat: (Double) -> Unit,
        setLng: (Double) -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(2000)
            setMaxUpdateDelayMillis(10000)
            setMaxUpdates(1)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val lat = location.latitude
                    val lng = location.longitude

                    setLat(lat)
                    setLng(lng)

                    addressTextView.text = "Lat: $lat, Lng: $lng"

                    getAddressFromLocation(lat, lng, addressTextView)

                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    private fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        addressTextView: TextView
    ) {
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

                val locationAddress = "$fullAddress, $city, $state, $pincode, $country"
                addressTextView.text = locationAddress
            } else {
                addressTextView.text = getString(R.string.address_not_found)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            addressTextView.text = getString(R.string.error_address)
        }
    }



    private fun collectSdrInsertResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.insertSdrApi) {
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

                                toastLong("Data save successfully")
                                findNavController().navigateUp()
                            } else if (response.responseCode == 401) {
                                AppUtil.showSessionExpiredDialog(findNavController(), requireContext())
                            }
                            else if (response.responseCode == 301) {
                                toastLong(response.responseDesc)

                            }
                            else {
                                toastLong(response.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }


}

