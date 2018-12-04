package me.martichou.be.grateful.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*

/**
 * Called to setup needed permission for Grateful
 */
fun setupPermissions(activity: Activity, context: Context) {
    val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
    }
}

/**
 * @return the time with DAY MONTH N° YEARS
 */
fun currentTime(): Date {
    return Calendar.getInstance().time
}

/**
 * @return a random number
 */
fun randomNumber(min: Int, max: Int): String {
    return Random().nextInt(max - min + 1 + min).toString()
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
            .setMinCropWindowSize(0, 0)
            .setMaxCropResultSize(4096, 3072)
            .setAspectRatio(4, 3)
            .start(context, fragment)
}