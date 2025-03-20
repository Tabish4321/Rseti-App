package com.rsetiapp.common.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rsetiapp.R
import com.rsetiapp.common.fragments.FollowUpCandidateFragmentDirections
import com.rsetiapp.common.model.response.CandidateDetail
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
            binding.tvSettlementStatus.text = decodeFollowUpStatus(candidate.sattleStatus ?: "1")
            displayStatus(binding.followUp1Status, candidate.quarter1 ?: "1")
            displayStatus(binding.followUp2Status, candidate.quarter2 ?: "1")
            displayStatus(binding.followUp3Status, candidate.quarter3 ?: "1")
            displayStatus(binding.followUp4Status, candidate.quarter4 ?: "1")
            displayStatus(binding.followUp5Status, candidate.quarter5 ?: "1")
            displayStatus(binding.followUp6Status, candidate.quarter6 ?: "1")
            displayStatus(binding.followUp7Status, candidate.quarter7 ?: "1")
            displayStatus(binding.followUp8Status, candidate.quarter8 ?: "1")

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

        private fun decodeFollowUpStatus(status: String): String = when (status) {
            "1" -> "Settlement In Progress"
            "2" -> "Settled"
            "3" -> "Unsettled"
            else -> "N/A"
        }

        private fun displayStatus(statusView: ImageView, status: String) {
            statusView.setColorFilter(
                ContextCompat.getColor(
                    statusView.context, when (status) {
                        "2" -> R.color.yellow
                        "3" -> R.color.color_follow_up_status
                        "4" -> R.color.color_red
                        else -> R.color.color_grey
                    }
                )
            )
        }
    }
}
