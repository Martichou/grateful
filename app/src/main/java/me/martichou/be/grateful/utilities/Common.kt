package me.martichou.be.grateful.utilities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import me.martichou.be.grateful.R
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Called to setup needed permission for Grateful
 */
fun setupPermissions(context: Context, activity: FragmentActivity) {
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
 * Open the keyboard and push the content to view the full edittext
 */
fun openKeyboard(dialog: Dialog) {
    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}

/**
 * Open the image cropper view
 */
fun imageCropper(context: Context, fragment: Fragment) {
    CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMinCropWindowSize(0, 0)
            .setMaxCropResultSize(3096, 2322)
            .setAspectRatio(4, 3)
            .start(context, fragment)
}

/**
 * Used to save image on the apps storage
 * Compressed cause it will lag if it's not.
 */
fun compressImage(activity: FragmentActivity?, viewModel: AddViewModel, file: File, add_btn: AppCompatImageButton) {
    runOnIoThread {
        val storageDir = activity!!.getDir("imgForNotes", Context.MODE_PRIVATE)
        val success = if (!storageDir.exists()) {
            storageDir.mkdirs()
        } else {
            true
        }
        if (success) {
            val imageFile = File(storageDir, viewModel.randomImageName)
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                if (!deleted) {
                    Log.i("Error", "Cannot delete the file..")
                }
            }
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(imageFile)
                BitmapFactory.decodeFile(file.absolutePath).compress(Bitmap.CompressFormat.JPEG, 50, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                    viewModel.changeHasPhoto(true)
                    viewModel.changeIsWorking(false)

                    activity.runOnUiThread {
                        add_btn.background = ContextCompat.getDrawable(activity, R.drawable.bg_roundaccent)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}