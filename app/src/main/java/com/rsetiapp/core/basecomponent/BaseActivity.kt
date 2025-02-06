package com.rsetiapp.core.basecomponent

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.R
import com.rsetiapp.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseActivity<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : AppCompatActivity() {

    private var _binding: VB? = null
    val binding: VB get() = _binding as VB

    private val progress: AlertDialog? by lazy {
        AppUtil.getProgressDialog(this)
    }

    @Inject
    lateinit var userPreferences: UserPreferences

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        _binding = bindingInflater.invoke(layoutInflater)

        if (_binding is ActivityWelcomeBinding) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        if (_binding == null) {
            throw IllegalArgumentException("Binding cannot be null")
        }
        setContentView(binding.root)
    }

    fun <T> collectActionFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            flow.collectLatest(collect)
        }
    }


    suspend fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            flow.collectLatest(collect)
        }
    }

    fun showProgress() {
        progress?.show()
    }

    fun hideProgress() {
        progress?.dismiss()
    }

    fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackBar.view.setPadding(0, 0, 0, 0)
        snackBar.view.elevation = 0f
        snackBar.view.background =
            ContextCompat.getDrawable(this, R
                .drawable.shape_rectangle_grey)
        snackBar.show()
    }

/*    fun showAlertDialogToOpenSetting(listen: BaseFragment.PermissionActionLister) {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.please_allow_required_permissions_from_setting))
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                dialog?.dismiss()
                listen.openSetting()
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog?.dismiss()
                listen.cancel()
            }
            .setCancelable(false)
            .create()
            .show()
    }*/

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    fun launchActivity(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass))
    }

    fun launchActivityClearTop(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    fun launchActivityClearTask(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
    fun launchActivityClearAndNewTask(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun launchActivity(activityClass: Class<out Any>, bundle: Bundle) {
        val intent = Intent(this, activityClass)
        intent.putExtras(bundle)
        startActivity(intent)
    }




}

