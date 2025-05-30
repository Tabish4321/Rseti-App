package com.rsetiapp.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.R
import com.rsetiapp.common.model.response.VisitData
import com.rsetiapp.databinding.ItemSdrDataBinding
class SdrAdapter(
    private val sdrList: List<VisitData>,
    private val onItemClick: (VisitData) -> Unit
) : RecyclerView.Adapter<SdrAdapter.SdrViewHolder>() {

    inner class SdrViewHolder(val binding: ItemSdrDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SdrViewHolder {
        val binding = ItemSdrDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SdrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SdrViewHolder, position: Int) {
        val item = sdrList[position]

        holder.binding.tvInstituteName.text = item.instituteName
        holder.binding.tvFinYear.text = holder.itemView.context.getString(R.string.fin_year) + "  ${item.finYear}"
        holder.binding.tvMonth.text = holder.itemView.context.getString(R.string.month) + "  ${getMonthName(item.month)}"
        holder.binding.tvStatus.text = item.sdrVisitStatus ?: "Not Available"
        holder.binding.statusImage.setImageResource(
            if (item.sdrVisitStatus.equals("Completed", ignoreCase = true)) R.drawable.ic_verified
            else R.drawable.baseline_pending_24
        )

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = sdrList.size

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }
    }
}
