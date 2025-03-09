package com.rsetiapp.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rsetiapp.R
import com.rsetiapp.common.model.request.Candidate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CandidateBottomSheetFragment(private val candidateList: MutableList<Candidate>, private val adapter: RecyclerView.Adapter<*>) :
    BottomSheetDialogFragment() {
    var selectedDate=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.candidate_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val etCandidateName = view.findViewById<EditText>(R.id.etCandidateName)
        val etMobileNo = view.findViewById<EditText>(R.id.etMobileNo)
        val etDob = view.findViewById<TextView>(R.id.etDob)
        val etGender = view.findViewById<EditText>(R.id.etGender)
        val etGuardianName = view.findViewById<EditText>(R.id.etGuardianName)
        val etGuardianMobile = view.findViewById<EditText>(R.id.etGuardianMobile)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)
        val btnAdd = view.findViewById<TextView>(R.id.btnAdd)
        val btnClose = view.findViewById<TextView>(R.id.btnClose)
        val llRecycler = requireActivity().findViewById<View>(R.id.llRecycler) // Get the layout


        etDob.setOnClickListener {

            showDatePicker(etDob)
        }
        btnAdd.setOnClickListener {

            if (etCandidateName.text.toString().isNotEmpty()&& etGender.text.toString().isNotEmpty()&&
            etGuardianName.text.toString().isNotEmpty()&& etGuardianMobile.text.toString().isNotEmpty()&&
            etAddress.text.toString().isNotEmpty()&& etMobileNo.text.toString().isNotEmpty()&&
            etDob.text.toString().isNotEmpty()){

                val candidate = Candidate("",
                    etCandidateName.text.toString(),
                    etGender.text.toString(),
                    etGuardianName.text.toString(),
                    etGuardianMobile.text.toString(),
                    etAddress.text.toString(),
                    etMobileNo.text.toString(),
                    etDob.text.toString(),""

                )

                candidateList.add(candidate)
                adapter.notifyItemInserted(candidateList.size - 1)
                llRecycler.visibility = View.VISIBLE

                Toast.makeText(requireContext(), "Candidate Added", Toast.LENGTH_SHORT).show()
                dismiss()

            }
            else
                Toast.makeText(requireContext(), "Kindly fill all details first", Toast.LENGTH_SHORT).show()


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

}