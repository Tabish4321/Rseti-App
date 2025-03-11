package com.rsetiapp.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.R
import com.rsetiapp.common.model.response.EapList

class EapAdapter(
    private val eapList: List<EapList>,
    private val onItemClick: (EapList) -> Unit // Pass clicked item
) : RecyclerView.Adapter<EapAdapter.EapViewHolder>() {

    class EapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eapName: TextView = view.findViewById(R.id.tvEapName)
        val monthYear: TextView = view.findViewById(R.id.tvMonthYear)
        val eapAddress: TextView = view.findViewById(R.id.tvEapAddress)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val statusImage: ImageView = view.findViewById(R.id.statusImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_eap, parent, false)
        return EapViewHolder(view)
    }

    override fun onBindViewHolder(holder: EapViewHolder, position: Int) {
        val eap = eapList[position]
        holder.eapName.text = eap.eapName
        holder.monthYear.text = eap.monthYear
        holder.eapAddress.text = eap.eapAddress
        holder.status.text = eap.status

        val context = holder.itemView.context
        when (eap.status) {
            "Completed" -> {

               // holder.status.setTextColor(ContextCompat.getColor(context, R.color.color_dark_light_green))
                holder.statusImage.setImageResource(R.drawable.ic_dark_verified)


            }
            "Expired" -> {

                //holder.status.setTextColor(ContextCompat.getColor(context, R.color.color_red))
                holder.statusImage.setImageResource(R.drawable.baseline_dangerous_24)


            }
            "Active" -> {

              //  holder.status.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                holder.statusImage.setImageResource(R.drawable.ic_verified)


            }
            else -> {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.color_red))


            }
        }


        // Add Click Listener
        holder.itemView.setOnClickListener {
            onItemClick(eap) // Pass clicked item
        }
    }

    override fun getItemCount(): Int = eapList.size
}
