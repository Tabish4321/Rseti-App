package com.rsetiapp.common.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.databinding.FragmentHomeBinding

class HomeFrahment :BaseFragment<FragmentHomeBinding>(FragmentHomeBinding :: inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){

        listener()
    }

    private fun listener() {



    }
}