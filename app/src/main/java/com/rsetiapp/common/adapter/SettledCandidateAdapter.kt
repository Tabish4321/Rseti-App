package com.rsetiapp.common.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rsetiapp.R
import com.rsetiapp.common.model.response.CandidateSettlementVerificationDetail
import com.rsetiapp.common.model.response.SettledCandidate
import com.rsetiapp.databinding.ItemSettletedCandidateDetailsBinding


class SettledCandidateAdapter(
    private val candidateList: List<SettledCandidate>,
    private val onItemClick: (SettledCandidate) -> Unit
) : RecyclerView.Adapter<SettledCandidateAdapter.CandidateViewHolder>() {

    inner class CandidateViewHolder(val binding: ItemSettletedCandidateDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(candidate:
                 SettledCandidate) {
//            binding.tvCandidateName.text = candidate.candidateName ?: "N/A"

            val context = binding.root.context

            Glide.with(context)
                .load(R.drawable.person) // Load default image if decoding fails
                .into(binding.candidateImage)






            binding.tvtvBatchRegNoValue.text = candidate.batchRegNo
            binding.tvRollNumberValue.text = candidate.rollNo.toString()
            binding.tvContactNumber.text = candidate.mobileNo
            binding.tvCandidateName.text = candidate.candidateName
            binding.tvCandidateIdValue.text = candidate.candidateId
            binding.tvguardianContactValue.text = candidate.guardianMobileNo
            binding.tvsettledOnValue.text = candidate.settledOn
            binding.tvverificationStatusValue.text = candidate.verificationStatus
            binding.root.setOnClickListener {
                onItemClick(candidate)
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding = ItemSettletedCandidateDetailsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CandidateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        holder.bind(candidateList[position])
    }

    override fun getItemCount() = candidateList.size
}


