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
import com.rsetiapp.R
import com.rsetiapp.common.model.request.Candidate

class CandidateBottomSheetFragment(private val candidateList: MutableList<Candidate>, private val adapter: RecyclerView.Adapter<*>) :
    BottomSheetDialogFragment() {

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
        val etDob = view.findViewById<EditText>(R.id.etDob)
        val etGender = view.findViewById<EditText>(R.id.etGender)
        val etGuardianName = view.findViewById<EditText>(R.id.etGuardianName)
        val etGuardianMobile = view.findViewById<EditText>(R.id.etGuardianMobile)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)
        val btnAdd = view.findViewById<TextView>(R.id.btnAdd)
        val btnClose = view.findViewById<TextView>(R.id.btnClose)
        val llRecycler = requireActivity().findViewById<View>(R.id.llRecycler) // Get the layout

        btnAdd.setOnClickListener {
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

}