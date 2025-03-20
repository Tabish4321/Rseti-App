package com.rsetiapp.common

import android.annotation.SuppressLint
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
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.widget.ImageView
import com.rsetiapp.BuildConfig
import com.rsetiapp.common.model.request.CandidateDetailsReq
import com.rsetiapp.common.model.request.CandidateSearchReq
import com.rsetiapp.common.model.response.CandidateData
import com.rsetiapp.common.model.response.CandidateSearchData
import com.rsetiapp.core.util.gone

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
    private lateinit var candidateNameSearch: TextView
    private lateinit var candidatePicSearch: ShapeableImageView
    private lateinit var llCandidateSearch: LinearLayout
    private lateinit var etCandidateName: EditText
    private lateinit var etGender: EditText
    private lateinit var etGuardianName: EditText
    private lateinit var etGuardianMobile: EditText
    private lateinit var etAddress: EditText
    private lateinit var etMobileNo: EditText
    private lateinit var etDob: TextView



    private var candidateSearchList: List<CandidateSearchData> = listOf()
    private var candidateDetailsList: List<CandidateData> = listOf()






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
        etGender = view.findViewById(R.id.etGender)
        etGuardianName = view.findViewById(R.id.etGuardianName)
        etGuardianMobile = view.findViewById(R.id.etGuardianMobile)
        etAddress = view.findViewById(R.id.etAddress)
        val btnAdd = view.findViewById<TextView>(R.id.btnAdd)
        val btnClose = view.findViewById<TextView>(R.id.btnClose)
        candidateNameSearch = view.findViewById(R.id.candidateNameSearch)
        candidatePicSearch = view.findViewById(R.id.candidatePicSearch)
        llCandidateSearch = view.findViewById(R.id.llSearch)





        val etSearch = view.findViewById<EditText>(R.id.etSearch)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val getCandidateId = s.toString()
                if (getCandidateId.length == 10) {
                    commonViewModel.candidateSearchListAPI(
                        CandidateSearchReq(
                            BuildConfig.VERSION_NAME,
                            getCandidateId
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


        llCandidateSearch.setOnClickListener {
            llCandidateSearch.gone()

            commonViewModel.candidateDetailsAPI(CandidateDetailsReq(BuildConfig.VERSION_NAME,candidateId))
            collectCandidateDataResponse()

        }




        btnAdd.setOnClickListener {
            candidateName = etCandidateName.text.toString()
            candidateGender = etGender.text.toString()
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
                    val candidate = Candidate(
                        candidateId,
                        candidateName,
                        candidateGender,
                        candidateGuardianName,
                        candidateGuardianMobile,
                        candidateAddress,
                        candidateMobileNo,
                        candidateDob,
                        ""
                    )

                    candidateList.add(candidate)
                    adapter.notifyItemInserted(candidateList.size - 1)

                    // Update the candidate count in the parent fragment
                    updateCandidateCount(candidateList.size)

                    Toast.makeText(requireContext(), "Candidate Added", Toast.LENGTH_SHORT).show()
                    dismiss()
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
        // Restrict to future dates only
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()) // Only future dates

        // Create Material Date Picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default today
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        // Show Date Picker
        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(Date(selection))
            textView.text = formattedDate
            selectedDate= formattedDate
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


                            }
                            else if (getcandidateDetailsRes.responseCode == 301) {
                                Toast.makeText(requireContext(), "Please Update from PlayStore", Toast.LENGTH_SHORT).show()


                            }
                            else {
                                Toast.makeText(requireContext(), getcandidateDetailsRes.responseDesc ?: "Error", Toast.LENGTH_SHORT).show()

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


}


