package com.rsetiapp.common.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.FogotPaasReq
import com.rsetiapp.common.model.request.OtpGenerateRequest
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.gone
import com.rsetiapp.core.util.onDone
import com.rsetiapp.core.util.showKeyboard
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.core.util.visible
import com.rsetiapp.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ForgotPasswordFragment: BaseFragment<FragmentForgotPasswordBinding>(FragmentForgotPasswordBinding:: inflate) {

    private val commonViewModel: CommonViewModel by activityViewModels()
    private var countDownTimer: CountDownTimer? = null
    private var otp = ""
    private var mobileNo = ""
    private var userId = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        init()

    }

    private fun init(){
        listener()
        resendOTPTimer()
        addTextWatchers()
        otpUI()

    }
    private fun  listener(){

        binding.backBtn.setOnClickListener {

            findNavController().navigateUp()
        }
        binding.tvVerify.setOnClickListener {

            validateAndNavigate()


            binding.et1.text.clear()
            binding.et2.text.clear()
            binding.et3.text.clear()
            binding.et4.text.clear()
        }


        binding.tvSendOtpAgain.setOnClickListener {
            binding.tvSendOtpAgain.isEnabled = false
            binding.tvSendOtpAgain.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_grey
                )
            )


            mobileNo= binding.etPhone.text.toString()
            userId= binding.etId.text.toString()

            if ( mobileNo.isNotEmpty()&& userId.isNotEmpty()){
                otp= AppUtil.generateOTP().toString()
                commonViewModel.generateOtpAPI(OtpGenerateRequest(BuildConfig.VERSION_NAME,userId,otp,mobileNo))
                collectOtpAndMobileVerifyResponse()



            }

            else toastShort("please fill all fields")

            resendOTPTimer()

        }


        binding.progressButton.centerButton.setOnClickListener {


            mobileNo= binding.etPhone.text.toString()

            userId= binding.etId.text.toString()

            if ( mobileNo.isNotEmpty()&& userId.isNotEmpty()){

                otp= AppUtil.generateOTP().toString()

                commonViewModel.generateOtpAPI(OtpGenerateRequest(BuildConfig.VERSION_NAME,userId,otp,mobileNo))
                collectOtpAndMobileVerifyResponse()

            }

            else toastShort("please fill all fields")


        }

    }

    private fun resendOTPTimer() {


        countDownTimer?.let {
            it.cancel()
        }

        countDownTimer = object : CountDownTimer(60000.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds: Long =
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - (minutes * 60)
                var minutesInString = minutes.toString()
                var secondsInString = seconds.toString()
                if (minutes.toString().length == 1) {
                    minutesInString = "0$minutes"
                } else if (minutes.toString().isEmpty()) {
                    minutesInString = "00"
                }
                if (seconds.toString().length == 1) {
                    secondsInString = "0$seconds"
                } else if (seconds.toString().isEmpty()) {
                    secondsInString = "00"
                }
                ("$minutesInString:$secondsInString").also {
                    binding.tvTimer.text = it
                }
            }

            override fun onFinish() {
                binding.tvSendOtpAgain.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_dark_green
                    )
                )
                binding.tvSendOtpAgain.isEnabled = true
            }
        }.start()
    }

    private fun addTextWatchers() {



        binding.etPhone.doOnTextChanged { text, start, before, count ->

            if (text?.length == 10) {
                binding.progressButton.root.visible()
            } else binding.progressButton.root.gone()

        }

        binding.et1.doOnTextChanged { text, _, _, _ ->
            if (text?.length == 1) {
                binding.et2.requestFocus()
                hasFocus(R.id.et2)
            } else {
                binding.et1.requestFocus()
                hasFocus(R.id.et1)
            }
            showVerifyButtonUI()
        }

        binding.et2.doOnTextChanged { text, _, _, _ ->
            if (text?.length == 1) {
                binding.et3.requestFocus()
                hasFocus(R.id.et3)
            } else {
                binding.et1.requestFocus()
                hasFocus(R.id.et1)
            }

            showVerifyButtonUI()
        }

        binding.et3.doOnTextChanged { text, _, _, _ ->
            if (text?.length == 1) {
                binding.et4.requestFocus()
                hasFocus(R.id.et4)
            } else {
                binding.et2.requestFocus()
                hasFocus(R.id.et2)
            }

            showVerifyButtonUI()
        }

        binding.et4.doOnTextChanged { text, _, _, _ ->
            if (text?.length == 1) {
                binding.et4.requestFocus()
                hasFocus(R.id.et4)
            } else {
                binding.et3.requestFocus()
                hasFocus(R.id.et3)
            }

            showVerifyButtonUI()
        }

    }

    private fun showVerifyButtonUI() {

        val et1Text = binding.et1.text.toString()
        val et2Text = binding.et2.text.toString()
        val et3Text = binding.et3.text.toString()
        val et4Text = binding.et4.text.toString()

        val hasToShow = "$et1Text$et2Text$et3Text$et4Text".length == 4

        if (hasToShow)
            binding.tvVerify.visible()
        else binding.tvVerify.gone()
    }

    private fun hasFocus(et: Int) {
        when (et) {
            R.id.et1 -> {
                binding.et1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_grey_rectangle)

                if (binding.et2.text.toString().length == 1)
                    binding.et2.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et3.text.toString().length == 1)
                    binding.et3.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et3.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et4.text.toString().length == 1)
                    binding.et4.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et4.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)


            }

            R.id.et2 -> {
                binding.et2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_grey_rectangle)

                if (binding.et1.text.toString().length == 1)
                    binding.et1.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et3.text.toString().length == 1)
                    binding.et3.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et3.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et4.text.toString().length == 1)
                    binding.et4.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et4.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)


            }

            R.id.et3 -> {
                binding.et3.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_grey_rectangle)

                if (binding.et1.text.toString().length == 1)
                    binding.et1.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et1.text.toString().length == 1)
                    binding.et2.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et4.text.toString().length == 1)
                    binding.et4.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et4.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

            }

            R.id.et4 -> {
                binding.et4.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)

                if (binding.et1.text.toString().length == 1)
                    binding.et1.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et2.text.toString().length == 1)
                    binding.et2.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

                if (binding.et3.text.toString().length == 1)
                    binding.et3.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_rectangle)
                else binding.et3.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_otp_invalid_rectangle)

            }

        }
    }
    private fun otpUI() {
        binding.et1.setOnClickListener {

            showKeyboard(binding.et1)
            hasFocus(R.id.et1)

        }
        binding.et2.setOnClickListener {

            showKeyboard(binding.et2)
            hasFocus(R.id.et2)

        }
        binding.et3.setOnClickListener {
            showKeyboard(binding.et3)
            hasFocus(R.id.et3)
        }
        binding.et4.setOnClickListener {
            showKeyboard(binding.et4)
            hasFocus(R.id.et4)
        }

        binding.et4.onDone {
            validateAndNavigate()
        }
    }

    private  fun validateAndNavigate(){

        if ("${binding.et1.text}${binding.et2.text}${binding.et3.text}${binding.et4.text}".contentEquals(
                otp
            )
        ) {
            binding.clForgotOTP.gone()

            // hit api


            commonViewModel.forgetPasswordAPI(FogotPaasReq(BuildConfig.VERSION_NAME,userId))
            collectForgotPassSendResponse()



        }
        else {
            toastLong("Invalid OTP")
            binding.et1.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_otp_invalid_rectangle
            )
            binding.et2.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_otp_invalid_rectangle
            )
            binding.et3.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_otp_invalid_rectangle
            )
            binding.et4.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_otp_invalid_rectangle
            )
        }



    }
    private fun collectOtpAndMobileVerifyResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.generateOtpAPI) {
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
                        it.data?.let { mobileVerifyRes ->
                            if (mobileVerifyRes.responseCode == 200) {
                                toastShort(mobileVerifyRes.responseDesc)
                                showSnackBar(otp)
                                binding.clForgotOTP.visible()
                                binding.tvVerify.visible()
                                binding.etPhone.gone()
                                binding.progressButton.root.gone()

                            } else if (mobileVerifyRes.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }

                            else if (mobileVerifyRes.responseCode == 302) {
                                toastShort(mobileVerifyRes.responseDesc)
                            }
                            else if (mobileVerifyRes.responseCode == 201) {
                                toastShort(mobileVerifyRes.responseDesc)
                            }




                            else {
                                showSnackBar("Something went wrong")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }

    private fun collectForgotPassSendResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.forgetPasswordAPI) {
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
                        it.data?.let { mobileVerifyRes ->
                            if (mobileVerifyRes.responseCode == 200) {
                                toastLong(mobileVerifyRes.responseDesc)
                                binding.clForgotOTP.visible()
                                binding.tvVerify.visible()
                                binding.etPhone.gone()
                                binding.progressButton.root.gone()
                                findNavController().navigateUp()

                            } else if (mobileVerifyRes.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }

                            else if (mobileVerifyRes.responseCode == 303) {
                                toastLong(mobileVerifyRes.responseDesc)
                            }





                            else {
                                showSnackBar("Something went wrong")
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }
                }
            }
        }
    }


}