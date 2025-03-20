package com.rsetiapp.common.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.R
import com.rsetiapp.common.model.request.Candidate
class CandidateAdapter(
    private var candidateList: MutableList<Candidate>,
    private val onDelete: (Int) -> Unit,
    private val onUpdateCount: (Int) -> Unit // Callback to update count
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
        holder.name.text = candidate.candidateName
        holder.mobile.text = candidate.mobileNo

        holder.deleteButton.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_delete_confirmation, null)
            val dialog = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
            val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnDelete.setOnClickListener {
                val position = holder.adapterPosition
                if (position in candidateList.indices) {
                    onDelete(position)
                    onUpdateCount(candidateList.size)
                }
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int = candidateList.size
}
