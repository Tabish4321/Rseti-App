package com.rsetiapp.common.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.onRightDrawableClicked
import com.rsetiapp.core.util.setRightDrawablePassword
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginFragmentBinding>(LoginFragmentBinding :: inflate ){
    private var userName = ""
    private var password = ""
    private var showPassword = true


    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        handleBackPress()
    }

    private  fun  init(){

        listener()
    }
    private fun listener(){

        binding.tvForgotPass.setOnClickListener {

            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())

        }
        binding.etPassword.onRightDrawableClicked {

            if (showPassword) {
                showPassword = false
                binding.etPassword.setRightDrawablePassword(
                    true, null, null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_open_eye), null
                )
            } else {
                showPassword = true

                binding.etPassword.setRightDrawablePassword(
                    false, null, null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.close_eye), null
                )

            }

        }


        binding.tvLogin.setOnClickListener {

            if (binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                userName = binding.etEmail.text.toString().uppercase()
                password = binding.etPassword.text.toString()
                val shaPass = AppUtil.sha512Hash(password)
                if (AppUtil.getSavedLanguagePreference(requireContext()).contains("eng")) {

                    AppUtil.saveLanguagePreference(requireContext(), "eng")


                } else
                    AppUtil.changeAppLanguage(
                        requireContext(),
                        AppUtil.getSavedLanguagePreference(requireContext())
                    )


                commonViewModel.getLoginAPI(LoginReq(userName,shaPass,AppUtil.getAndroidId(requireContext()),BuildConfig.VERSION_NAME,""))

                collectLoginResponse()

            } else
                showSnackBar("Please enter id and password")


        }

    }



    private fun collectLoginResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getLoginAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            toastShort(baseErrorResponse.message)
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getLoginResponse ->
                            when (getLoginResponse.responseCode) {
                                200 -> {

                                    userPreferences.updateUserId(null)
                                    userPreferences.updateUserId(userName)
                                    userPreferences.saveUserName(getLoginResponse.wrappedList[0].userName)
                                    AppUtil.saveLoginStatus(requireContext(), true)

                                    // findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainHomePage())

                                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFrahment())

                                }

                                203 -> {
                                    showSnackBar(getLoginResponse.responseDesc)

                                }

                                301 -> {
                                    showSnackBar(getLoginResponse.responseDesc)
                                }

                                else -> {
                                    showSnackBar("Something went wrong")
                                }
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                private var backPressedTime: Long = 0
                private val exitInterval = 2000 // 2 seconds

                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < exitInterval) {
                        isEnabled =
                            false // Disable callback to let the system handle the back press
                        requireActivity().finish()
                    } else {
                        backPressedTime = currentTime
                        showSnackBar("Press back again to exit")
                    }
                }
            })
    }

}