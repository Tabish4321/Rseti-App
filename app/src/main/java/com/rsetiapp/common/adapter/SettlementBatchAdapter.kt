package com.rsetiapp.common.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.common.fragments.FollowUpCandidateFragmentDirections
import com.rsetiapp.common.fragments.SettlementVeryficationFragmentDirections
import com.rsetiapp.common.model.response.SettlementPercentage
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.databinding.ItemBatchBinding
import com.rsetiapp.databinding.ItemSettlementVeryficationBatchBinding

//class SettlementBatchAdapter(
//    private val list: MutableList<SettlementPercentage>,
//    private val onItemClick: (Int) -> Unit
//) : RecyclerView.Adapter<SettlementBatchAdapter.ViewHolder>()
//{
//
//    var selectedPosition = -1
//
//    inner class ViewHolder(val binding: ItemBatchBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemBatchBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int = list.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val item = list[position]
//        holder.binding.tvBatchName.text = item.batchName
//
//        // 🔴 Highlight selected item
//        if (position == selectedPosition) {
//            holder.itemView.setBackgroundColor(Color.RED)
//        } else {
//            holder.itemView.setBackgroundColor(Color.BLACK)
//        }
//
//        holder.itemView.setOnClickListener {
//            selectedPosition = position
//            notifyDataSetChanged()
//            onItemClick(position)
//        }
//    }
//}
class SettlementBatchAdapter(
    private val candidateList: MutableList<SettlementPercentage>
) : RecyclerView.Adapter<SettlementBatchAdapter.CandidateViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding =
            ItemSettlementVeryficationBatchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CandidateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        holder.bind(candidateList[position])
    }

    override fun getItemCount(): Int = candidateList.size

    /** ✅ THIS is where update() is implemented */
    fun update(newList: List<SettlementPercentage>) {
        candidateList.clear()
        candidateList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class CandidateViewHolder(
        private val binding: ItemSettlementVeryficationBatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(candidate: SettlementPercentage) {

            binding.tvBatchIdName.text = candidate.batchRegNo

            binding.tvSettlementPerctangeName.text = candidate.settledPercentage
            binding.tvInstituteName.text = candidate.instituteName
            binding.tvBatchName.text = candidate.batchName
            binding.tvtvCandidateedpType.text = candidate.edpType
            binding.tvSettlementStatus.text = candidate.status
            val context = binding.root.context
            binding.root.setOnClickListener {
                AppUtil.saveRecyclerViewPreference(context, "false")
                if (position != RecyclerView.NO_POSITION && position < candidateList.size) {
                    val data = candidateList[position]

                    candidate.batchId?.let { candidateId -> AppUtil.savebatchIdIdPreference(context,candidateId) }
                    AppUtil.saveCandidateIdPreference(context,""+data.candidateId)
                    val action = SettlementVeryficationFragmentDirections
                        .actionSettlementVeryficationBatchFragmentToSettlemt(data)
                    binding.root.findNavController().navigate(action)
                }



            }
        }
    }
}