package com.rsetiapp.core.basecomponent

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputLayout

abstract class BaseDialog<VB : ViewBinding>(private val bindingInflater: (inflater: LayoutInflater) -> VB) :
    DialogFragment(), View.OnClickListener {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding as VB


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null)
            throw IllegalArgumentException("Binding cannot be null")
        return binding.root
    }

    override fun onClick(v: View) {
    }

    fun setTextInputLayoutHintColor(
        textInputLayout: TextInputLayout,
        context: Context,
        @ColorRes colorIdRes: Int
    ) {
        textInputLayout.defaultHintTextColor =
            ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }

    open fun showDialog(manager: FragmentManager, tag: String?, fragment: Fragment?) {
        val ft = manager.beginTransaction()
        ft.add(fragment!!, tag)
        ft.commit()
    }

}
