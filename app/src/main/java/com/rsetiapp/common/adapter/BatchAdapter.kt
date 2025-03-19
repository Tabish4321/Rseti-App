package com.rsetiapp.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController

import androidx.recyclerview.widget.RecyclerView

import com.rsetiapp.common.fragments.FollowUpBatchFragmentDirections
import com.rsetiapp.common.model.response.Batch
import com.rsetiapp.databinding.ItemBatchBinding

class BatchAdapter(
    private val batchList: List<Batch>
) : RecyclerView.Adapter<BatchAdapter.BatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding = ItemBatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        val batch = batchList[position]
        holder.bind(batch)
    }

    override fun getItemCount(): Int = batchList.size

    inner class BatchViewHolder(private val binding: ItemBatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(batch: Batch) {
            binding.tvBatchIdName.text = batch.batchCode
            binding.tvBatchName.text = batch.batchName  // Show Batch Name

            // Handle Click
            binding.root.setOnClickListener {
                val data = batchList[adapterPosition]

                val action =
                    FollowUpBatchFragmentDirections.actionFollowUpBatchFragmentToFollowUpCandidateFragment(
                        data.batchCode ?: "0", data.batchName ?: "Batch Name"
                    )
                binding.root.findNavController().navigate(action)
            }
        }
    }
}
