package com.rsetiapp.common.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.common.model.request.LoginReq
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.AESCryptography
import com.rsetiapp.core.util.AppConstant
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.onRightDrawableClicked
import com.rsetiapp.core.util.setRightDrawablePassword
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginFragmentBinding>(LoginFragmentBinding :: inflate ){
    private var userName = ""
    private var userNameActual = ""
    private var password = ""
    private var token = ""
    private var saltPassword = ""
    private var showPassword = true
    private var isApiCalled = false

   /* private val startForAuthentication =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val status = data?.getStringExtra(AppConstant.Constants.RESULT_STATUS) ?: "failure"
                val message = data?.getStringExtra(AppConstant.Constants.RESULT_MESSAGE) ?: "Unknown error"

                Log.d("", "status: $status , message $message")

                if (status == "success") {
                    toastShort("✅ Success: $message")
                } else {
                    toastShort("❌ Failure: $message")
                }
            } else {
                toastShort("⚠️ Authentication cancelled")
            }
        }*/

    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())
        init()
        handleBackPress()
    }

    private  fun  init(){

        listener()
    }
    private fun listener(){

// Disable long-press (prevents copy-paste menu)
        binding.etPassword.setOnLongClickListener { true }

// Prevents context menu actions (copy, cut, paste)
        binding.etPassword.customSelectionActionModeCallback = object : android.view.ActionMode.Callback {
            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: android.view.Menu?): Boolean = false
            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: android.view.Menu?): Boolean = false
            override fun onActionItemClicked(mode: android.view.ActionMode?, item: android.view.MenuItem?): Boolean = false
            override fun onDestroyActionMode(mode: android.view.ActionMode?) {}
        }

// Disable clipboard pasting, but allow normal keyboard inputs
        binding.etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val clipboard = v.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("", "")) // Clear clipboard
            }
        }

// Disable drag-and-drop text pasting
        binding.etPassword.setOnDragListener { _, _ -> true }

// Prevent programmatic clipboard pasting
        binding.etPassword.setTextIsSelectable(false) // Prevents text selection
        binding.etPassword.isLongClickable = false


        binding.tvForgotPass.setOnClickListener {




            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())

           /* val userId = binding.etEmail.text.toString().trim()
            val userName = binding.etPassword.text.toString().trim()
            if (userId.isNotEmpty()) {
                startAuthentication(AppConstant.Constants.CALL_TYPE_REGISTRATION, userId)
            } else {
                toastShort("Please enter User ID")
            }*/



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
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty() && !isApiCalled) {
                        isApiCalled = true
                        commonViewModel.getToken(AppUtil.getAndroidId(requireContext()), BuildConfig.VERSION_NAME)
                        collectTokenResponse()

                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.tvLogin.setOnClickListener {
            if (AppUtil.getSavedLanguagePreference(requireContext()).contains("en")) {

                AppUtil.saveLanguagePreference(requireContext(), "en")


            } else
                AppUtil.changeAppLanguage(
                    requireContext(),
                    AppUtil.getSavedLanguagePreference(requireContext())
                )


            if (binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                userName = binding.etEmail.text.toString().uppercase()
                password = binding.etPassword.text.toString()

                val shaPass = AppUtil.sha512Hash(password)
                val saltPass = shaPass+saltPassword
                val finalPass = AppUtil.sha512Hash(saltPass)



                commonViewModel.getLoginAPI(LoginReq(userName,finalPass,AppUtil.getAndroidId(requireContext()),BuildConfig.VERSION_NAME,""))

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
                                    val token1 = AESCryptography.decryptIntoString(getLoginResponse.appCode,AppConstant.Constants.ENCRYPT_KEY,AppConstant.Constants.ENCRYPT_IV_KEY)

                                   /* userPreferences.updateUserId(null)
                                    userPreferences.updateUserId(userName)
                                    userPreferences.saveUserName(getLoginResponse.wrappedList[0].loginId)
                                    AppUtil.saveLoginStatus(requireContext(), true)
*/
                                    if (token == token1){
                                        AppUtil.saveTokenPreference(requireContext(),"Bearer "+getLoginResponse.appCode)
                                        userPreferences.updateUserId(null)
                                        userPreferences.updateUserId(userName)
                                        userPreferences.saveUserName(getLoginResponse.wrappedList[0].userName)
                                        AppUtil.saveLoginStatus(requireContext(), true)  // true means user is logged in

                                       /* for (x in getLoginResponse.wrappedList){

                                            userNameActual=  x.userName
                                        }
*/




                                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFrahment())


                                    }
                                    else toastShort("Session expired")

                                }

                                203 -> {
                                    commonViewModel.getToken(AppUtil.getAndroidId(requireContext()), BuildConfig.VERSION_NAME)
                                    showSnackBar(getLoginResponse.responseDesc)
                                    showSnackBar(getLoginResponse.responseMsg)

                                }

                                301 -> {
                                  //  showSnackBar(getLoginResponse.responseDesc)
                                    showUpdateDialog()
                                }

                                else -> {
                                    showSnackBar(getLoginResponse.responseDesc)
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
    private fun collectTokenResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getToken) {
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
                        it.data?.let { getToken ->
                            when (getToken.responseCode) {
                                200 -> {

                                    token= AESCryptography.decryptIntoString(getToken.authToken,
                                        AppConstant.Constants.ENCRYPT_KEY,AppConstant.Constants.ENCRYPT_IV_KEY)

                                    saltPassword= AESCryptography.decryptIntoString(getToken.passString,
                                        AppConstant.Constants.ENCRYPT_KEY,AppConstant.Constants.ENCRYPT_IV_KEY)

                                }

                                301 -> {

                                    showSnackBar(getToken.responseDesc)


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
    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(requireContext()) // 🔥 use requireContext() inside Fragment
        builder.setTitle("Update Available")
        builder.setMessage("A new version of the app is available. Please update to continue.")

        builder.setPositiveButton("Update") { dialog, _ ->
            val appPackageName = "com.kaushalpanjee"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                intent.setPackage("com.android.vending")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName&hl=en_IN"))
                startActivity(intent)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setCancelable(false)
        builder.create().show()
    }

   /* private fun startAuthentication(callType: String, userId: String) {
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        intent.putExtra(AppConstant.Constants.EXTRA_CLIENT_ID, AppConstant.Constants.YOUR_CLIENT_ID)
        intent.putExtra(AppConstant.Constants.EXTRA_CALL_TYPE, callType)
        intent.putExtra(AppConstant.Constants.EXTRA_USER_ID, userId)
        if (callType == AppConstant.Constants.CALL_TYPE_REGISTRATION) {
            intent.putExtra(AppConstant.Constants.EXTRA_USER_NAME, "TestUser")
        }
        startForAuthentication.launch(intent)
    }*/
}