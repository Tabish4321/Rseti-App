package com.rsetiapp.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rsetiapp.R
import com.rsetiapp.common.model.request.Candidate
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.material.imageview.ShapeableImageView
import com.rsetiapp.core.util.visible
import kotlinx.coroutines.flow.collectLatest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.textfield.TextInputLayout
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.model.request.CandidateDetailsReq
import com.rsetiapp.common.model.request.CandidateSearchReq
import com.rsetiapp.common.model.request.CourseRequest
import com.rsetiapp.common.model.request.SdrListReq
import com.rsetiapp.common.model.response.CandidateData
import com.rsetiapp.common.model.response.CandidateSearchData
import com.rsetiapp.common.model.response.CourseItem
import com.rsetiapp.common.model.response.CourseResponse
import com.rsetiapp.common.model.response.SalaryRange
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.gone
import com.rsetiapp.core.util.removeAllWhitespaces
import com.rsetiapp.core.util.toastLong
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class CandidateBottomSheetFragment(private val candidateList: MutableList<Candidate>, private val adapter: RecyclerView.Adapter<*>,
                                   private val updateCandidateCount: (Int) -> Unit) :
    BottomSheetDialogFragment() {
    var selectedDate=""
    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var progressBar: View
    private var candidateId= ""
    private var candidateName= ""
    private var candidateGender= ""
    private var candidateGuardianName= ""
    private var candidateGuardianMobile= ""
    private var candidateAddress= ""
    private var candidateMobileNo= ""
    private var candidateDob= ""
    private var selectedCourseId= ""
    private lateinit var candidateNameSearch: TextView
    private lateinit var candidatePicSearch: ShapeableImageView
    private lateinit var llCandidateSearch: LinearLayout
    private lateinit var AddCandidate: TextView
    private lateinit var etCandidateName: EditText
    private lateinit var etGender: EditText
    private lateinit var etGuardianName: EditText
    private lateinit var etGuardianMobile: EditText
    private lateinit var etAddress: EditText
    private lateinit var etMobileNo: EditText
    private lateinit var etDob: TextView
    private lateinit var notIntresCandImage: ImageView
    private lateinit var etCourse: TextView
    private lateinit var spinnerGender: AutoCompleteTextView
    private lateinit var genderHiding: TextInputLayout
    lateinit var userPreferences: UserPreferences

    private lateinit var photoFile: File
    private lateinit var imageUri: Uri
    private var candidateNotInImage: String = ""

    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                notIntresCandImage.setImageURI(imageUri)
                convertToBase64(photoFile)
            }
        }

    private var candidateSearchList: List<CandidateSearchData> = listOf()
    private var candidateDetailsList: List<CandidateData> = listOf()


    private lateinit var genderAdapterr: ArrayAdapter<String>
    private val genderList = listOf("Male", "Female","Others")
    private val selectedIndices = mutableSetOf<Int>()
    private var courseList: List<CourseItem> = listOf()
    private var courseNameList = ArrayList<String>()
    private var courseIdList = ArrayList<String>()





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        return inflater.inflate(R.layout.candidate_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        progressBar = view.findViewById(R.id.progressBar)
        etCandidateName = view.findViewById(R.id.etCandidateName)
        etMobileNo = view.findViewById(R.id.etMobileNo)
        etDob = view.findViewById(R.id.etDob)
        etCourse = view.findViewById(R.id.etCourse)
        etGender = view.findViewById(R.id.etGender)
        spinnerGender = view.findViewById(R.id.spinnerGender)
        genderHiding = view.findViewById(R.id.genderHiding)
        etGuardianName = view.findViewById(R.id.etGuardianName)
        etGuardianMobile = view.findViewById(R.id.etGuardianMobile)
        etAddress = view.findViewById(R.id.etAddress)
        val btnAdd = view.findViewById<TextView>(R.id.btnAdd)
        val btnClose = view.findViewById<TextView>(R.id.btnClose)
        candidateNameSearch = view.findViewById(R.id.candidateNameSearch)
        candidatePicSearch = view.findViewById(R.id.candidatePicSearch)
        llCandidateSearch = view.findViewById(R.id.llSearch)
        AddCandidate = view.findViewById(R.id.btnAddCandidate)
        notIntresCandImage = view.findViewById(R.id.notIntresCandImage)
        userPreferences = UserPreferences(requireContext())


        notIntresCandImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
            }
        }



        commonViewModel.courseEapApi(
            AppUtil.getSavedTokenPreference(requireContext()),
            CourseRequest(
                userPreferences.getUseID(),
                BuildConfig.VERSION_NAME,
                AppUtil.getAndroidId(requireContext())

            )
        )

        collectCourseResponse()
        //account adapter
        genderAdapterr = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            genderList
        )
        spinnerGender.setAdapter(genderAdapterr)



        spinnerGender.setOnItemClickListener { parent, view, position, id ->
            candidateGender = parent.getItemAtPosition(position).toString()
        }



        etCourse.setOnClickListener {
            val listView = ListView(requireContext()).apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_multiple_choice,
                    courseNameList
                )
                choiceMode = ListView.CHOICE_MODE_MULTIPLE

                // Initialize checked items based on previous selections
                selectedIndices.forEach { setItemChecked(it, true) }
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Select up to 3 courses")
                .setView(listView)
                .setPositiveButton("OK") { _, _ ->
                    // Update selectedIndices based on current checked state
                    selectedIndices.clear()
                    for (i in 0 until courseNameList.size) {
                        if (listView.isItemChecked(i)) {
                            selectedIndices.add(i)
                        }
                    }
                    // Reflect in TextView
                    etCourse.text = selectedIndices.map { courseNameList[it] }.joinToString(", ")
                    selectedCourseId = selectedIndices.map { courseIdList[it] }.joinToString(", ")
                    Toast.makeText(requireContext(), selectedCourseId, Toast.LENGTH_SHORT).show()

                }
                .setNegativeButton("Cancel", null)
                .create()

            listView.setOnItemClickListener { _, _, position, _ ->
                // If already selected, deselect
                if (selectedIndices.contains(position)) {
                    selectedIndices.remove(position)
                } else {
                    if (selectedIndices.size >= 3) {
                        // Prevent selecting more than 3
                        listView.setItemChecked(position, false)
                        Toast.makeText(requireContext(), "Only 3 selections allowed", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedIndices.add(position)
                    }
                }
            }

            dialog.show()
        }




        val etSearch = view.findViewById<EditText>(R.id.etSearch)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val getCandidateId = s.toString()
                if (getCandidateId.length == 10) {
                    commonViewModel.candidateSearchListAPI(AppUtil.getSavedTokenPreference(requireContext()),
                        CandidateSearchReq(
                            BuildConfig.VERSION_NAME,
                            getCandidateId,AppUtil.getAndroidId(requireContext()),userPreferences.getUseID()
                        )
                    )
                    collectCandidateSearchResponse()

                }
                else   llCandidateSearch.gone()


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        etDob.setOnClickListener {

            showDatePicker(etDob)
        }


        AddCandidate.setOnClickListener {
            llCandidateSearch.gone()

            commonViewModel.candidateDetailsAPI(AppUtil.getSavedTokenPreference(requireContext()),CandidateDetailsReq(BuildConfig.VERSION_NAME,candidateId
            ,AppUtil.getAndroidId(requireContext()),userPreferences.getUseID()))
            collectCandidateDataResponse()

        }




        btnAdd.setOnClickListener {

            candidateName = etCandidateName.text.toString()

            if (etGender.text.toString()!=""){
                candidateGender = etGender.text.toString()
            }
            candidateGuardianName = etGuardianName.text.toString()
            candidateGuardianMobile = etGuardianMobile.text.toString()
            candidateAddress = etAddress.text.toString()
            candidateMobileNo = etMobileNo.text.toString()
            candidateDob = etDob.text.toString()

            if (candidateName.isNotEmpty() && candidateGender.isNotEmpty() &&
                candidateGuardianName.isNotEmpty() && candidateGuardianMobile.isNotEmpty() &&
                candidateAddress.isNotEmpty() && candidateMobileNo.isNotEmpty() &&
                candidateDob.isNotEmpty()
            ) {
                if (AppUtil.isValidMobileNumber(etMobileNo.text.toString()) &&
                    AppUtil.isValidMobileNumber(etGuardianMobile.text.toString())
                ) {
                    val isDuplicate = candidateId.isNotBlank() && candidateList.any { it.candidateId == candidateId }

                    if (isDuplicate) {
                        Toast.makeText(requireContext(), "Candidate ID already added", Toast.LENGTH_SHORT).show()
                    } else {

                        if (candidateId.isNotBlank()){

                            val candidate = Candidate(
                                candidateId,
                                candidateName,
                                candidateGender,
                                candidateGuardianName,
                                candidateGuardianMobile,
                                candidateAddress,
                                candidateMobileNo,
                                candidateDob,
                                candidateNotInImage.removeAllWhitespaces(),
                                selectedCourseId
                            )

                            candidateList.add(candidate)
                            adapter.notifyItemInserted(candidateList.size - 1)

                            // Update the candidate count in the parent fragment
                            updateCandidateCount(candidateList.size)

                            Toast.makeText(requireContext(), "Candidate Added", Toast.LENGTH_SHORT).show()
                            dismiss()

                        }

                        else{

                            if (candidateNotInImage==""){
                                Toast.makeText(requireContext(), "Kindly Capture candidate photo", Toast.LENGTH_SHORT).show()

                            }
                            else{
                                val candidate = Candidate(
                                    candidateId,
                                    candidateName,
                                    candidateGender,
                                    candidateGuardianName,
                                    candidateGuardianMobile,
                                    candidateAddress,
                                    candidateMobileNo,
                                    candidateDob,
                                    candidateNotInImage.removeAllWhitespaces(),
                                    selectedCourseId
                                )

                                candidateList.add(candidate)
                                adapter.notifyItemInserted(candidateList.size - 1)

                                // Update the candidate count in the parent fragment
                                updateCandidateCount(candidateList.size)

                                Toast.makeText(requireContext(), "Candidate Added", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }

                        }


                    }

                } else {
                    Toast.makeText(requireContext(), "Mobile number is invalid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Kindly fill all details first", Toast.LENGTH_SHORT).show()
            }
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        // Set full width for bottom sheet
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Prevent dismissing on outside touch
        dialog?.setCanceledOnTouchOutside(false)
        isCancelable = false
    }
    private fun showDatePicker(textView: TextView) {
        // Get today's time in milliseconds
        val calendar = Calendar.getInstance()

        val ageLimit = AppUtil.getSavedEapCanAgeLimitPreference(requireContext())
        // Calculate the date 15 years ago
        calendar.add(Calendar.YEAR, ageLimit.toInt())
        val fifteenYearsAgoMillis = calendar.timeInMillis

        // Set calendar constraints: allow only dates up to 15 years ago
        val constraintsBuilder = CalendarConstraints.Builder()
            .setEnd(fifteenYearsAgoMillis) // 👈 Maximum selectable date
            .setValidator(DateValidatorPointBackward.before(fifteenYearsAgoMillis)) // 👈 Only dates before or equal

        // Create Material Date Picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date of Birth")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        // Show Date Picker
        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = sdf.format(Date(selection))
            textView.text = formattedDate
            selectedDate = formattedDate
        }
    }

    private fun collectCandidateSearchResponse() {
        lifecycleScope.launch {
            commonViewModel.candidateSearchListAPI.collectLatest { it ->
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getSearchRes ->
                            if (getSearchRes.responseCode == 200) {

                                llCandidateSearch.visible()
                                candidateSearchList= getSearchRes.wrappedList


                                for (x in candidateSearchList) {
                                    candidateId= x.candidateId
                                    candidateNameSearch.text = x.candidateName
                                    setBase64ToImageView(x.candidateProfilePic,candidatePicSearch)
                                }

                            }
                            else if (getSearchRes.responseCode == 301) {
                                Toast.makeText(requireContext(), "Please Update from PlayStore", Toast.LENGTH_SHORT).show()


                            }
                            else if (getSearchRes.responseCode == 201) {
                                Toast.makeText(requireContext(), getSearchRes.responseDesc, Toast.LENGTH_SHORT).show()


                            }
                            else if (getSearchRes.responseCode == 401) {
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())

                            }

                            else toastLong(getSearchRes.responseDesc)
                        } ?:   Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    private fun collectCandidateDataResponse() {
        lifecycleScope.launch {
            commonViewModel.candidateDetailsAPI.collectLatest { it ->
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getcandidateDetailsRes ->
                            if (getcandidateDetailsRes.responseCode == 200) {

                                candidateDetailsList= getcandidateDetailsRes.wrappedList

                                for (x in candidateDetailsList) {

                                    etCandidateName.setText(x.candidateName)
                                    etGender.setText(x.gender)
                                    etGuardianName.setText(x.guardianName)
                                    etGuardianMobile.setText(x.guardianMobileNo)
                                    etAddress.setText(x.candidateAddress)
                                    etMobileNo.setText(x.mobileNo)
                                    etDob.text = x.dob
                                    candidateName= x.candidateName
                                    candidateGender =x.gender
                                    candidateGuardianName =x.guardianName
                                    candidateGuardianMobile = x.guardianMobileNo
                                    candidateAddress= x.candidateAddress
                                    candidateMobileNo= x.mobileNo
                                    candidateDob = x.dob
                                    candidateId= x.candidateId

                                }
                                etGender.visible()
                                genderHiding.gone()



                            }
                            else if (getcandidateDetailsRes.responseCode == 301) {
                                Toast.makeText(requireContext(), "Please Update from PlayStore", Toast.LENGTH_SHORT).show()


                            }
                            else if (getcandidateDetailsRes.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getcandidateDetailsRes.responseDesc)
                            }
                        } ?:   Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
    private fun setBase64ToImageView(base64String: String?, imageView: ImageView) {
        if (base64String.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.person) // Default placeholder

            return
        }
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.setImageResource(R.drawable.person)
        }
    }


    private fun collectCourseResponse() {
        lifecycleScope.launch {
            commonViewModel.courseEapApi.collectLatest { it ->
                when (it) {
                    is Resource.Loading -> Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT)
                        .show()

                    //showProgressBar()
                    is Resource.Error -> {
                        //  hideProgressBar()
                        Toast.makeText(
                            requireContext(),
                            "Internal Server Error",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    is Resource.Success -> {
                        //  hideProgressBar()
                        it.data?.let { getCourseDetails ->
                            if (getCourseDetails.responseCode == 200) {
                                courseList = getCourseDetails.wrappedList



                                for (x in courseList) {
                                    courseNameList.add(x.courseName)
                                    courseIdList.add(x.courseCode)
                                }

                            }
                            else if (getCourseDetails.responseCode == 301) {
                                Toast.makeText(
                                    requireContext(),
                                    getCourseDetails.responseMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (getCourseDetails.responseCode == 401) {
                                AppUtil.showSessionExpiredDialog(
                                    findNavController(),
                                    requireContext()
                                )
                            }

                            else if (getCourseDetails.responseCode == 202) {
                                Toast.makeText(
                                    requireContext(),
                                    getCourseDetails.responseDesc,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                            else toastLong(getCourseDetails.responseDesc)

                        } ?: Toast.makeText(
                            requireContext(),
                            "Internal Server Error",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }
    }


    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            photoFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            imageCaptureLauncher.launch(cameraIntent)
        }
    }

    private fun createImageFile(): File {
        val fileName = "IMG_${System.currentTimeMillis()}"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    private fun convertToBase64(file: File) {
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()
            candidateNotInImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            // You can now pass this base64 string back to the activity or ViewModel
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}


