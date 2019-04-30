package me.martichou.be.grateful.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * @return the current time time in millis
 */
fun dateDefault(): String {
    return Date().time.toString()
}

/**
 * @return the current dd/MM/yyyy
 */
fun dateToSearch(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}

/**
 * @return dd/MM/yyyy from a date object
 */
fun formatTodateToSearch(calendar: Calendar): String {
    val sdf = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
    return sdf.format(calendar.time)
}

/**
 * @return date from string
 */
fun stringToDate(aDate: String?): Date? {
    if (aDate == null) return null
    val simpledateformat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return simpledateformat.parse(aDate)
}

/**
 * @return a random number
 */
fun randomNumber(min: Int, max: Int): String {
    return Random.nextInt(max - min + 1 + min).toString()
}

/**
 * Convert float to dp
 */
fun convertDpToPixel(dp: Float, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * Set status bar to translucent
 */
fun statusBarTrans(activity: FragmentActivity?) {
    val window = activity?.window
    window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

/**
 * Set status bar to white
 */
fun statusBarWhite(activity: FragmentActivity?) {
    val window = activity?.window
    window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}