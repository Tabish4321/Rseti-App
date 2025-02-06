@file:OptIn(ExperimentalContracts::class)

package com.rsetiapp.core.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * View visibility extensions
 */

fun View.gone() = run { visibility = View.GONE }

fun View.visible() = run { visibility = View.VISIBLE }

fun View.invisible() = run { visibility = View.INVISIBLE }

infix fun View.visibleIf(condition: Boolean) =
    run { visibility = if (condition) View.VISIBLE else View.GONE }

infix fun View.goneIf(condition: Boolean) =
    run { visibility = if (condition) View.GONE else View.VISIBLE }

infix fun View.invisibleIf(condition: Boolean) =
    run { visibility = if (condition) View.INVISIBLE else View.VISIBLE }


/**
 * Snake bar extensions
 */

fun View.snackBar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun View.snackBar(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

/**
 * Toast extensions
 */

fun Fragment.toastShort(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastLong(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Activity.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Hide keyBoard extensions
 */

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Fragment.hideKeyboard() {
    activity?.apply {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun Activity.showKeyboard(editText: EditText) {
    val imgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.showKeyboard(editText: EditText) {
    activity?.apply {
        val imgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

}

/**
 * Digit, alphanumeric & alphabets check extensions of string
 */

val String.isDigitOnly: Boolean
    get() = matches(Regex("^\\d*\$"))

val String.isAlphabeticOnly: Boolean
    get() = matches(Regex("^[a-zA-Z]*\$"))

val String.isAlphanumericOnly: Boolean
    get() = matches(Regex("^[a-zA-Z\\d]*\$"))

/**
 * Remove white space from string extension
 */

fun String.removeAllWhitespaces(): String {
    return this.replace("\\s+".toRegex(), "")
}

fun String.removeDuplicateWhitespaces(): String {
    return this.replace("\\s+".toRegex(), " ")
}

/**
 * Check is object null extension
 */

val Any?.isNull get() = this == null

/**
 * if object null than execute block of code
 */

fun Any?.ifNull(block: () -> Unit) = run {
    if (this == null) {
        block()
    }
}

/**
 * Date format extensions
 */

fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.parse(this)
}

fun Date.toStringFormat(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(this)
}

/**
 * Check permission granted extension
 */

fun Context.isPermissionGranted(permission: String) = run {
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * System service manager extensions
 */

val Context.windowManager
    get() = ContextCompat.getSystemService(this, WindowManager::class.java)

val Context.connectivityManager
    get() = ContextCompat.getSystemService(this, ConnectivityManager::class.java)

val Context.notificationManager
    get() = ContextCompat.getSystemService(this, NotificationManager::class.java)

val Context.downloadManager
    get() = ContextCompat.getSystemService(this, DownloadManager::class.java)

/**
 * copy to clip board extension
 */

fun String.copyToClipboard(context: Context) {
    val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("clipboard", this)
    clipboardManager?.setPrimaryClip(clip)
}

/**
 * Boolean extensions
 */

@OptIn(ExperimentalContracts::class)
fun Boolean?.isTrue(): Boolean {
    contract {
        returns(true) implies (this@isTrue != null)
    }
    return this == true
}

@OptIn(ExperimentalContracts::class)
fun Boolean?.isFalse(): Boolean {
    contract {
        returns(true) implies (this@isFalse != null)
    }
    return this == false
}

val Boolean?.orTrue: Boolean
    get() = this ?: true

val Boolean?.orFalse: Boolean
    get() = this ?: false

/**
 * log extensions
 */

fun log(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, "$tag: $message")
    }
}


fun getTime(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

fun changeTimeFormat24To12Hours(dateIn24Hours: String): String {
    val f1: DateFormat = SimpleDateFormat("HH:mm:ss") //HH for hour of the day (0 - 23)
    val d: Date? = f1.parse(dateIn24Hours)
    val f2: DateFormat = SimpleDateFormat("h:mma")
    if (d != null)
        return f2.format(d).lowercase() // "12:18am"
    else return ""

}

fun Boolean.toInt() = if (this) 1 else 0

fun Int.toBoolean() = this == 1

fun EditText.onDone(callback: () -> Unit) {
    // These lines optional if you don't want to set in Xml
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        }
        false
    }
}
/*

fun ImageView.uploadLogoImage(imgUrl: String) {
    Glide.with(this)
        .load(imgUrl)
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .dontTransform()
        .into(this)
}

fun ImageView.loadImage(file: File?, error_image: Int = R.drawable.ic_upload_with_text) {
    file?.let {
        Glide.with(this)
            .load(it)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(error_image)
            .skipMemoryCache(true)
            .placeholder(error_image)
            .into(this)
    }
}

fun ImageView.loadImage(url: String?, error_image: Int = R.drawable.ic_upload_with_text) {
    url?.let {
        Glide.with(this)
            .`as`(PictureDrawable::class.java)
            .load(it.trim())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(error_image)
            .into(this)
    }
}
*/


fun Context.isLocationEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        (getSystemService(
            AppCompatActivity.LOCATION_SERVICE
        ) as LocationManager).isLocationEnabled
    } else {
        false
    }
}

fun getDeviceName(): String {
    return Build.MODEL
}

fun getDeviceSuperInfo(): String {
    var s = ""
    try {
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + Build.VERSION.SDK_INT;
        s += "\n Device: " + Build.DEVICE;
        s += "\n Model (and Product): " + Build.MODEL + " (" + Build.PRODUCT + ")";
        s += "\n RELEASE: " + Build.VERSION.RELEASE;
        s += "\n BRAND: " + Build.BRAND;
        s += "\n DISPLAY: " + Build.DISPLAY;
        s += "\n CPU_ABI: " + Build.CPU_ABI;
        s += "\n CPU_ABI2: " + Build.CPU_ABI2;
        s += "\n UNKNOWN: " + Build.UNKNOWN;
        s += "\n HARDWARE: " + Build.HARDWARE;
        s += "\n Build ID: " + Build.ID;
        s += "\n MANUFACTURER: " + Build.MANUFACTURER;
        s += "\n SERIAL: " + Build.SERIAL;
        s += "\n USER: " + Build.USER;
        s += "\n HOST: " + Build.HOST;

        log("DeviceInfo: ", s)

    } catch (e: Exception) {
        e.message?.let { log("DeviceInfo: ", it) }
    }
    return s
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}
fun EditText.setRightDrawablePassword(isOpen: Boolean, leftDrawable : Drawable ?= null,
                                      topDrawable : Drawable ? = null, rightDrawable: Drawable? = null,
                                      bottomDrawable: Drawable?=null) {
    if (isOpen) {
        setCompoundDrawablesWithIntrinsicBounds(
            leftDrawable,
            topDrawable,
            rightDrawable,
            bottomDrawable
        )
        inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        transformationMethod = HideReturnsTransformationMethod.getInstance()

    } else {
        setCompoundDrawablesWithIntrinsicBounds(
            leftDrawable,
            topDrawable,
            rightDrawable,
            bottomDrawable
        )
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
    }


    val typeface = ResourcesCompat.getFont(this.context, R.font.avenir_next_medium)
    this.setSelection(this.text.length)
    setTypeface(typeface)
}


fun TextView.setDrawable(leftDrawable : Drawable ?= null,
                                      topDrawable : Drawable ? = null, rightDrawable: Drawable? = null,
                                      bottomDrawable: Drawable?=null) {
    setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )
}


fun TextView.setUnderline(text: String) {
    this.text = SpannableString(text).apply {
        setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun setSpannableTextWithColor(
    view: TextView,
    resourceId: Int,
    username: String,
    userColor: Int,
    messageCount: Int,
    countColor: Int
) {
    val fullText = view.context.getString(resourceId, username, messageCount)
    log("fullText", fullText)

    view.setText(fullText, TextView.BufferType.SPANNABLE)

    val spannable = view.text as Spannable

    val usernameIndex = fullText.indexOf(username)

    spannable.setSpan(
        ForegroundColorSpan(userColor),
        usernameIndex,
        usernameIndex + username.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val countIndex = fullText.indexOf(messageCount.toString())

    spannable.setSpan(
        ForegroundColorSpan(countColor),
        countIndex,
        countIndex + messageCount.toString().length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun createSpannableString(
    text: String,
    styles: List<StyleConfig>,
    clickListeners: List<(View) -> Unit>? = null
): SpannableString {

    val spannableString = SpannableString(text)
    for (style in styles) {
        spannableString.setSpan(
            style.span,
            style.start,
            style.end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannableString
}

data class StyleConfig(val start: Int = 0, val end: Int = 0, val span: Any)


fun Context.checkPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun bitmapToDrawable(context: Context, bitmap: Bitmap): Drawable {
    return BitmapDrawable(context.resources, bitmap)
}

fun EditText.setLeftDrawable(context: Context,icon : Int){

    val drawableEnd = ContextCompat.getDrawable(context, icon)
    this.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableEnd, null)

}

/**
 * Creates a half-circle progress bitmap that visually represents a progress bar with customizable attributes.
 * The progress bar includes:
 * - A background (inactive) arc.
 * - A progress (active) arc based on the current progress value.
 * - A circle at the end of the progress arc that indicates the completion point.
 * - A text displaying the current progress percentage in bold at the center.
 *
 * @param width The width of the bitmap.
 * @param height The height of the bitmap.
 * @param progress The current progress value (0-100).
 * @param backgroundColor The color of the background (inactive) arc.
 * @param progressColor The color of the progress (active) arc.
 * @param backgroundStrokeWidth The stroke width of the background arc.
 * @param progressStrokeWidth The stroke width of the progress arc.
 * @param textColor The color of the progress text.
 * @param progressEndCircleColor The color of the circle at the end of the progress arc.
 *
 * @return A Bitmap object representing the half-circle progress bar.
 *
 * @example
 * val progressBitmap = createHalfCircleProgressBitmap(
 *     width = 300,
 *     height = 300,
 *     progress = 70.0f,
 *     backgroundColor = Color.GRAY,
 *     progressColor = Color.GREEN,
 *     backgroundStrokeWidth = 20.0f,
 *     progressStrokeWidth = 25.0f,
 *     textColor = Color.BLACK,
 *     progressEndCircleColor = Color.RED
 * )
 * imageView.setImageBitmap(progressBitmap)
 */
fun createHalfCircleProgressBitmap(
    width: Int,                // Width of the bitmap
    height: Int,               // Height of the bitmap
    progress: Float,           // Progress (0-100)
    backgroundColor: Int,      // Color for the background arc
    progressColor: Int,        // Color for the progress arc
    backgroundStrokeWidth: Float, // Stroke width for the background arc
    progressStrokeWidth: Float,   // Stroke width for the progress arc
    textColor: Int,            // Color for the progress text
    progressEndCircleColor: Int // Color for the circle at the end of the progress stroke
): Bitmap {
    // Create an empty bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Calculate the center and radius for the arcs
    val centerX = width / 2f
    val centerY = height / 2f
    val outerRadius = (minOf(width, height) / 2f) - maxOf(backgroundStrokeWidth, progressStrokeWidth) // Adjust for max stroke width

    // Paint for the background arc
    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.STROKE
        strokeWidth = backgroundStrokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    // Paint for the progress arc
    val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = progressColor
        style = Paint.Style.STROKE
        strokeWidth = progressStrokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    // Paint for the progress text
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = outerRadius / 3f // Text size proportional to the radius
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    // Paint for the circle at the end of the progress arc
    val endCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = progressEndCircleColor
        style = Paint.Style.FILL
    }

    // Draw the background arc (full half-circle)
    val backgroundRect = RectF(
        centerX - outerRadius,
        centerY - outerRadius,
        centerX + outerRadius,
        centerY + outerRadius
    )
    canvas.drawArc(backgroundRect, 180f, 180f, false, backgroundPaint)

    // Draw the progress arc
    val sweepAngle = (progress / 100) * 180
    canvas.drawArc(backgroundRect, 180f, sweepAngle, false, progressPaint)

    // Draw the circle at the end of the progress arc
    if (progress > 0) {
        val endAngle = Math.toRadians(180.0 + sweepAngle) // Convert angle to radians
        val endX = (centerX + outerRadius * Math.cos(endAngle)).toFloat()
        val endY = (centerY + outerRadius * Math.sin(endAngle)).toFloat()

        // Calculate the radius of the end circle
        val endCircleRadius = progressStrokeWidth / 2f // Set the end circle radius equal to half of the progress stroke width

        // Draw the end circle
        canvas.drawCircle(endX, endY, endCircleRadius+5, endCirclePaint)
    }

    // Draw the progress text at the center of the bitmap
    val progressText = "${progress.toInt()}%"
    val fontMetrics = textPaint.fontMetrics
    val textY = centerY - (fontMetrics.ascent + fontMetrics.descent) / 2 // Center text vertically
    canvas.drawText(progressText, centerX, textY, textPaint)

    return bitmap
}

fun decodeBase64(base64String: String): String? {
    return try {
        // Decode the Base64 string to a byte array
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        // Convert the byte array to a string
        String(decodedBytes, Charsets.UTF_8)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null // Return null if decoding fails
    }
}













