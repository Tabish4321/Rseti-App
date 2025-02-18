package com.rsetiapp.common.adapter

import ChildAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.R
import com.rsetiapp.common.model.response.Module
import com.rsetiapp.databinding.ItemParentBinding
class ParentAdapter(
    private val moduleList: List<Module>
) : RecyclerView.Adapter<ParentAdapter.ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemParentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = moduleList[position]
        holder.bind(module)
    }

    override fun getItemCount(): Int = moduleList.size

    inner class ModuleViewHolder(private val binding: ItemParentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Module) {
            binding.tvModuleName.text = module.moduleName  // Show Module Name

            // Handle Click to Expand/Collapse RecyclerView
            binding.tvModuleName.setOnClickListener {
                module.isExpanded = !module.isExpanded  // Toggle Expansion
                notifyItemChanged(adapterPosition)  // Update UI
            }

            // Set up child RecyclerView (Nested RecyclerView for Forms)
            if (module.isExpanded) {
                binding.rvChild.visibility = View.VISIBLE  // Expand
                val childAdapter = ChildAdapter(module.forms)
                binding.rvChild.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvChild.adapter = childAdapter
            } else {
                binding.rvChild.visibility = View.GONE  // Collapse
            }
        }
    }
}
