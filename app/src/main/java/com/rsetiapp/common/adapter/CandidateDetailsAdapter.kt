package com.rsetiapp.common.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rsetiapp.R
import com.rsetiapp.common.fragments.FollowUpCandidateFragmentDirections
import com.rsetiapp.common.model.response.CandidateDetail
import com.rsetiapp.core.util.AppUtil
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

        fun bind(candidate: CandidateDetail?) {
            if (candidate == null) {
                return // Prevents NullPointerException
            }

            val context = binding.root.context

            // Handle Profile Picture
            val profilePic = candidate.candidateProfilePic
            if (profilePic.isNullOrEmpty() || profilePic == "NA") {
                Glide.with(context)
                    .load(R.drawable.person)
                    .into(binding.candidateImage)
            } else {
                try {
                    val decodedString: ByteArray = Base64.decode(profilePic, Base64.DEFAULT)
                    val profileBitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                    Glide.with(context)
                        .load(profileBitmap)
                        .into(binding.candidateImage)
                } catch (e: Exception) {
                    Glide.with(context)
                        .load(R.drawable.person) // Load default image if decoding fails
                        .into(binding.candidateImage)
                }
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


            // Handle Click Navigation (Ensure safe `adapterPosition`)
            binding.root.setOnClickListener {
                val position = adapterPosition
                val quat1 = candidateList[position].quarter1
                val quat2 = candidateList[position].quarter2
                val quat3 = candidateList[position].quarter3
                val quat4= candidateList[position].quarter4
                val quat5 = candidateList[position].quarter5
                val quat6 = candidateList[position].quarter6
                val quat7 = candidateList[position].quarter7
                val quat8= candidateList[position].quarter8
                if (quat1=="2" || quat2=="2" || quat3=="2" || quat4=="2" || quat5=="2" || quat6=="2" || quat7=="2" || quat8=="2")
                {

                    if (position != RecyclerView.NO_POSITION && position < candidateList.size) {
                        val data = candidateList[position]

                        val action = FollowUpCandidateFragmentDirections
                            .actionFollowUpCandidateFragmentToFollowUpFormFragment(data)

                        binding.root.findNavController().navigate(action)
                    }

                }

                else{

                    AppUtil.showAlertDialog(context,"Alert","Followup has been already done. Please wait until next quarter. ")
                }

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
