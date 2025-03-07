package com.rsetiapp.common.adapter

import FollowUpStatusAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rsetiapp.common.fragments.FollowUpCandidateFragmentDirections
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.databinding.ItemCandidateDetailsBinding

class CandidateDetailsAdapter(
    private val candidateList: List<CandidateDetail>, private val batchId: String
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
            Glide.with(binding.root.context).load(candidate.profileImage)
                .into(binding.candidateImage)
            binding.tvCandidateName.text = candidate.name
            binding.tvCandidateRollNumber.text = "Roll No: ${candidate.rollNumber}"
            binding.tvCandidateContactNumber.text = "Contact number: ${candidate.contactNumber}"
            binding.rvStatus.apply {
                adapter = FollowUpStatusAdapter(candidate.followUpStatus)
            }

            // Handle Click
            binding.root.setOnClickListener {
                val data = candidateList[adapterPosition]

                val action =
                    FollowUpCandidateFragmentDirections.actionFollowUpCandidateFragmentToFollowUpFormFragment(
                        batchId, data
                    )
                binding.root.findNavController().navigate(action)
            }
        }
    }
}
