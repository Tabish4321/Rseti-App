package com.rsetiapp.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import android.content.res.Configuration
import android.provider.Settings
import com.google.gson.Gson
import com.rsetiapp.R
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Month
import java.time.format.TextStyle


object AppUtil {

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context) : String{

        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun sha512Hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))

        // Convert bytes to hex string
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun createFileName(userId: Int?): String {
        return "${userId}_${System.currentTimeMillis()}.jpg"
    }

    // Add this function to your class
    fun convertUriToBase64(uri: Uri,context: Context): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun getTimeZone(): String {
        return TimeZone.getDefault().id
    }

    fun getTimeZoneOffset():Int{
        val offset: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZonedDateTime.now().offset.totalSeconds/60
        } else {
            val tz = TimeZone.getDefault()
            val cal = GregorianCalendar.getInstance(tz)
            tz.getOffset(cal.timeInMillis)/1000*60
        }
        return offset
    }

    fun getAndroidDeviceInfo():String{
        return "MODEL : ${Build.MODEL}, MANUFACTURER : ${Build.MANUFACTURER}, DEVICE : ${Build.DEVICE}"
    }

    fun getProgressDialog(context: Context?): AlertDialog? {
        if (context == null) return null
        return MaterialAlertDialogBuilder(context)
            .setView(R.layout.layout_progress)
            .setBackground(ColorDrawable(Color.TRANSPARENT))
            .setCancelable(false)
            .create()
    }


    fun changeAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode) // For example, "en" for English, "es" for Spanish, etc.
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale) // Set the locale for the app

        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    fun getLoginStatus(context: Context): Boolean {
        // Get the SharedPreferences instance
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Retrieve the login status (false is the default value if not found)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }


    fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
        // Get the SharedPreferences instance
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Save the login status
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()  // Use apply() for asynchronous saving
    }



    fun showAlertDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
        }
        return false
    }


    fun formatScheduleTimeIntoDateTime(dateTimeString: String): Pair<String, String> {
        // Parse the input string into a ZonedDateTime object
        val zonedDateTime = ZonedDateTime.parse(dateTimeString)

        // Define the formatter for the date part
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

        // Format the date part
        val formattedDate = zonedDateTime.format(dateFormatter)

        // Define the formatter for the time part
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mma", Locale.ENGLISH)

        // Format the time part
        val formattedTime = zonedDateTime.format(timeFormatter).lowercase()

        return Pair(formattedDate, formattedTime)
    }


    fun parseDateTime(dateString: String, timeString: String): String {
        // Define the formatter for parsing the input date
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
        // Parse the date string into a LocalDateTime object
        val datePart = LocalDateTime.parse(dateString, dateFormatter)

        // Define the formatter for parsing the input time
        val timeFormatter = DateTimeFormatter.ofPattern("h : mm a", Locale.ENGLISH)
        // Parse the time string into a LocalDateTime object
        val timePart = LocalDateTime.parse(timeString, timeFormatter)

        // Combine the date and time parts
        val combinedDateTime = datePart.withHour(timePart.hour).withMinute(timePart.minute)

        // Format the combined date and time to ISO format with 'Z' to indicate UTC time
        val isoDateTime = combinedDateTime.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

        return isoDateTime
    }


    fun formatUtcDateTimeIntoReminderDate(input: String): String {
        // Parse the input date-time string
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val parsedDateTime = LocalDateTime.parse(input, inputFormatter)

        // Define the output formatter
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mma", Locale.ENGLISH)

        // Format the parsed date-time
        val formattedDateTime = parsedDateTime.format(outputFormatter)

        return formattedDateTime
    }

    fun formatUtcDateTimeIntoReminderDateNew(input: String): String {
        // Parse the input date-time string without milliseconds and timezone
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
        val parsedDateTime = ZonedDateTime.parse(input, inputFormatter)

        // Define the output formatter (without timezone information)
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mma", Locale.ENGLISH)

        // Format the parsed date-time
        val formattedDateTime = parsedDateTime.format(outputFormatter)

        return formattedDateTime
    }


    fun formatReminderDateIntoUtcDateTime(input: String): String {

        val inputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mma", Locale.ENGLISH)
        val parsedDateTime = LocalDateTime.parse(input, inputFormatter)

        // Convert the local date-time to a ZonedDateTime in the system default time zone
        val localZonedDateTime = parsedDateTime.atZone(ZoneId.systemDefault())

        // Convert the ZonedDateTime to UTC
        val utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"))

        // Define the output formatter
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        // Format the parsed date-time
        val formattedDateTime = utcZonedDateTime.format(outputFormatter)

        return formattedDateTime
    }

    fun combineDateTime(date: String, time: String): String {
        // Parse the date and time strings
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mma")

        val parsedDate = LocalDate.parse(date, dateFormatter)
        val parsedTime = LocalTime.parse(time, timeFormatter)

        // Combine date and time
        val dateTime = parsedDate.atTime(parsedTime)

        // Define the output formatter
        ///val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        ///val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

        // Format the combined date-time
        val formattedDateTime = dateTime.format(outputFormatter)

        return formattedDateTime//.toLowerCase()  // Convert AM/PM to lowercase as per your requirement
    }

    fun formatTimeToLowercaseAMPM(inputTime: String): String {
        // Check if the input time ends with "AM" or "PM"
        return when {
            inputTime.endsWith("AM", ignoreCase = true) -> inputTime.replace("AM", "am")
            inputTime.endsWith("PM", ignoreCase = true) -> inputTime.replace("PM", "pm")
            else -> inputTime // Return the original string if it doesn't end with AM or PM
        }
    }

    fun convertUTCtoIST(utcFormat: String, istFormat: String, dateToFormat: String): String{
        val utcFormat: DateFormat = SimpleDateFormat(utcFormat)
        utcFormat.timeZone = TimeZone.getTimeZone("GMT")

        val indianFormat: DateFormat = SimpleDateFormat(istFormat)
        utcFormat.timeZone = TimeZone.getTimeZone("IST")

        val timestamp: Date = utcFormat.parse(dateToFormat)
        return indianFormat.format(timestamp)
    }


    fun getCurrentDateTime():String{
        val calendar = Calendar.getInstance();
        val dateFormat =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.format(calendar.getTime());

    }

    fun getCurrentDate():String {
        val calendar = Calendar.getInstance();
        val dateFormat =  SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(calendar.getTime());

    }

    fun getCurrentYear(): Int {
        return LocalDate.now().year
    }

    fun convertMonthNumberToFullName(monthNumber: Int): String {
        val month = Month.of(monthNumber) // Convert MM to Month Enum
        return month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) // Get full month name (MMMM)
    }

    fun extractYearFromDate(completionDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(completionDate, formatter)
        return date.year.toString()
    }

    fun extractMonthFromDate(completionDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(completionDate, formatter)
        return date.month.toString()
    }

    fun saveLanguagePreference(context: Context, languageCode: String) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language_code", languageCode)
        editor.apply()
    }

    fun getSavedLanguagePreference(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language_code", "en") ?: "en" // Default to English
    }




    inline fun <reified T> fromJson(json: String): T {
        val gson = Gson()
        return gson.fromJson(json, T::class.java)
    }

    fun <T> toJson(model: T): String {
        val gson = Gson()
        return gson.toJson(model)
    }

    fun generateOTP(): Int {
        val secureRandom = SecureRandom()
        return secureRandom.nextInt(9000) + 1000 // Ensures a 4-digit number (1000 - 9999)
    }

   fun isValidMobileNumber(mobileNumber: String): Boolean {
        val regex = "^[6789]\\d{9}$".toRegex()
        return mobileNumber.matches(regex)
    }
}