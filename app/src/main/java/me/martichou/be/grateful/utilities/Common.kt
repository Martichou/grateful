package me.martichou.be.grateful.utilities

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Called to setup needed permission for Grateful
 */
fun setupPermissions(activity: AppCompatActivity, context: Context) {
    val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
    }
}

/**
 * @return the time with DAY MONTH N° YEARS
 */
fun currentTime(): String {
    return SimpleDateFormat("EE ha", Locale.getDefault()).format(Date()).toLowerCase().capitalize().replace(".", ",")
}

/**
 * @return the current month
 */
fun dateToSearch(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}

/**
 * From 02/02/2019 -> 2 Feb 2019
 */
fun formatDate(aDate: String?): String? {
    if (aDate == null) return null
    return SimpleDateFormat("d MMM y", Locale.getDefault()).format(stringToDate(aDate)).replace(". ", " ")
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
 * Little fun to show off a Toast
 */
fun makeToast(c: Context, s: String) {
    Toast.makeText(c, s, Toast.LENGTH_SHORT).show()
}

/**
 * Open the image cropper view
 */
fun imageCropper(context: Context, fragment: Fragment) {
    CropImage.activity()
        .setGuidelines(CropImageView.Guidelines.ON)
        .start(context, fragment)
}

/**
 * Set status bar to translucent
 */
fun statusBarTrans(activity: FragmentActivity) {
    val window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
}

/**
 * Set status bar to white
 */
fun statusBarWhite(activity: FragmentActivity) {
    val window: Window = activity.window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
}