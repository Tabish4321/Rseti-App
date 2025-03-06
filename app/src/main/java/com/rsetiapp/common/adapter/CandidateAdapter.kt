package com.rsetiapp.common.adapter

import Candidate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.R
class CandidateAdapter(
    private var candidateList: MutableList<Candidate>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>() {

    class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvCandidateName)
        val mobile: TextView = itemView.findViewById(R.id.tvCandidateMobile)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidateList[position]
        holder.name.text = candidate.name
        holder.mobile.text = candidate.mobile
        holder.deleteButton.setOnClickListener {
            onDelete(holder.adapterPosition) // Ensure proper position is used
        }
    }

    override fun getItemCount(): Int = candidateList.size
}
