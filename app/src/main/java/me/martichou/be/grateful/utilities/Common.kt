package me.martichou.be.grateful.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
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
            .setAspectRatio(3, 4)
            .start(context, fragment)
}

fun roundProfile(image: AppCompatImageView){
    image.outlineProvider = object : ViewOutlineProvider() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun getOutline(view: View, outline: Outline?) {
            outline?.setOval(0, 0, view.width,view.height)
        }
    }
    image.clipToOutline = true
}