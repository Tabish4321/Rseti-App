package com.rsetiapp.common.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.MySattelementBottomSheet

import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.Resource
import com.rsetiapp.databinding.FragmentFollowUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class FollowUpFormFragment :
    BaseFragment<FragmentFollowUpBinding>(FragmentFollowUpBinding::inflate) {

    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var sattelmentStatusAdapter: ArrayAdapter<String>

    private var selectedDate: String = ""
    private var selectedSettlemenItem: String = ""
    private val settalmentNameList = listOf("Settled", "Settlement InProgress", "UnSettled")

    private lateinit var candidate: CandidateDetail


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init()

        listener()
    }

    private fun init() {
        candidate = arguments?.getSerializable("candidate") as CandidateDetail

        binding.tvFormName.text = candidate.candidateName

        if (candidate.candidateProfilePic == "NA") {
            Glide.with(binding.root.context).load(R.drawable.person)
                .into(binding.candidateImage)
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

        listener()
    }

    private fun listener() {
        // Back Button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }


        sattelmentStatusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            settalmentNameList
        )

        binding.spinnerStatus.setAdapter(sattelmentStatusAdapter)


        binding.spinnerStatus.setOnItemClickListener { parent, view, position, id ->
            selectedSettlemenItem = parent.getItemAtPosition(position).toString()
            if (selectedSettlemenItem== "Settled") {


                val bottomSheet = MySattelementBottomSheet()
                bottomSheet.show(parentFragmentManager, "MyBottomSheet")


            }
        }

        //Submit Button
        /*binding.btnSubmit.setOnClickListener {

            collectInsertResponse()
        }*/


        //Adapter Program

        /* programNameAdapter = ArrayAdapter(
             requireContext(),
             android.R.layout.simple_spinner_dropdown_item,
             programName
         )

         binding.spinnerProgramName.setAdapter(programNameAdapter)*/


        /*binding.tvDate.setOnClickListener {

            showDatePicker(binding.tvDate)

        }*/
    }

    private fun collectInsertResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.insertEAPAPI) {
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


    private fun showDatePicker(textView: TextView) {
        // Restrict to future dates only
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()) // Only future dates

        // Create Material Date Picker
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select a Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default today
            .setCalendarConstraints(constraintsBuilder.build()).build()

        // Show Date Picker
        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(Date(selection))
            textView.text = formattedDate
            selectedDate = formattedDate
        }
    }


}