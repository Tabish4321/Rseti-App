package com.rsetiapp.common.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.R
import com.rsetiapp.common.model.response.CandidateEapData

class CandidateAdapter(
    private val onDeleteClick: (CandidateEapData) -> Unit
) : RecyclerView.Adapter<CandidateAdapter.ViewHolder>() {

    private val candidateList = mutableListOf<CandidateEapData>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCandidateName)
        val tvDob: TextView = itemView.findViewById(R.id.tvCandidateDob)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidate, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = candidateList[position]

        holder.tvName.text = item.candidateName
        holder.tvDob.text = item.dateOfBirth

        holder.btnDelete.setOnClickListener {

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Candidate")
                .setMessage("Are you sure you want to delete this candidate?")
                .setPositiveButton("Yes") { _, _ ->
                    onDeleteClick(item)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = candidateList.size

    fun updateList(list: List<CandidateEapData>) {

        candidateList.clear()
        candidateList.addAll(list)

        notifyDataSetChanged()
    }
}