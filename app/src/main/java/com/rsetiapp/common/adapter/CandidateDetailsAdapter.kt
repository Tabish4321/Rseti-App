package com.rsetiapp.common.adapter

import FollowUpStatusAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rsetiapp.R
import com.rsetiapp.common.fragments.FollowUpCandidateFragmentDirections
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.common.model.response.FollowUpStatus
import com.rsetiapp.databinding.ItemCandidateDetailsBinding


class CandidateDetailsAdapter(
    private val candidateList: List<CandidateDetail>
) : RecyclerView.Adapter<CandidateDetailsAdapter.CandidateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding =
            ItemCandidateDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CandidateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidateList[position]
        holder.bind(candidate)
    }

    override fun getItemCount(): Int = candidateList.size

    inner class CandidateViewHolder(private val binding: ItemCandidateDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(candidate: CandidateDetail) {
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
            binding.tvRollNumberValue.text = candidate.rollNo.toString()
            binding.tvContactNumber.text = candidate.mobileNo
            binding.rvStatus.apply {
                adapter = FollowUpStatusAdapter(
                    candidate.followUpStatus ?: ArrayList<FollowUpStatus>().apply {
                        add(FollowUpStatus("S1", "Settled"))
                        add(FollowUpStatus("S2", "Settled"))
                        add(FollowUpStatus("S3", "Settled"))
                        add(FollowUpStatus("S4", "Settled"))
                        add(FollowUpStatus("S5", "Not Settled"))
                        add(FollowUpStatus("S6", "Not Settled"))
                        add(FollowUpStatus("S7", "Not Settled"))
                        add(FollowUpStatus("S8", "Not Settled"))
                    })
            }

            // Handle Click
            binding.root.setOnClickListener {
                val data = candidateList[adapterPosition]

                val action =
                    FollowUpCandidateFragmentDirections.actionFollowUpCandidateFragmentToFollowUpFormFragment(
                        data
                    )
                binding.root.findNavController().navigate(action)
            }
        }
    }
}
