package com.rsetiapp.common.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rsetiapp.common.CommonViewModel

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
    private var selectedDate: String = ""
    private lateinit var candidate: CandidateDetail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {
        binding.tvFormName.text = "Follow Up Form"

        candidate = arguments?.getSerializable("candidate") as CandidateDetail
        Glide.with(requireContext()).load(candidate.profileImage).into(binding.candidateImage)
        binding.tvCandidateName.text = candidate.name
        binding.tvCareOfName.text = candidate.careOf
        binding.tvContactName.text = candidate.contactNumber

        listener()
    }

    private fun listener() {
        // Back Button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
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