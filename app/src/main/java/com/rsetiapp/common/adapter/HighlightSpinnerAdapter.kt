package com.rsetiapp.common.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HighlightSpinnerAdapter(
    context: Context,
    private val list: List<String>,
    private val getSelectedPosition: () -> Int
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val tv = view.findViewById<TextView>(android.R.id.text1)

        tv.setTextColor(
            if (position == getSelectedPosition()) Color.RED else Color.BLACK
        )
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view.findViewById<TextView>(android.R.id.text1)

        tv.setTextColor(
            if (position == getSelectedPosition()) Color.RED else Color.BLACK
        )
        return view
    }
}