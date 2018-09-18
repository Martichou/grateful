package me.martichou.be.grateful.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.martichou.be.grateful.R
import me.martichou.be.grateful.viewmodels.AddViewModel
import me.martichou.be.grateful.viewmodels.EditViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

/**
 * @return the time with DAY MONTH N° YEARS
 */
fun currentTime(): String {

    val calendar = Calendar.getInstance()
    val date = calendar.time
    val time = date.time
    val currentdayofmonth = calendar.get(Calendar.DAY_OF_MONTH)
    val year = calendar.get(Calendar.YEAR)
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(time)
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(time)

    return "$dayName, $monthName $currentdayofmonth, $year"
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
fun makeToast(c: Context, s: String){
    Toast.makeText(c, s, Toast.LENGTH_SHORT).show()
}

/**
 * Called to setup needed permission for Grateful
 */
fun setupPermissions(context: Context, activity: FragmentActivity) {
    val permission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    if (permission != PackageManager.PERMISSION_GRANTED) {
        Log.i("PermissionRequest", "Permission to write external storage denied !")
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            101
        )
    }
}

/**
 * Used to save image on the apps storage
 * Compressed cause it will lag if it's not.
 * TODO - In both update or save: --> Make the random number dependant on the id
 */
@SuppressLint("CheckResult")
fun compressImage(context: Context?, viewModel: AddViewModel, file: File, add_btn: Button) {
    Compressor(context)
        .setQuality(50)
        .setCompressFormat(Bitmap.CompressFormat.WEBP)
        .compressToFileAsFlowable(file)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            val storageDir = context!!.getDir("imgForNotes", Context.MODE_PRIVATE)

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
                        Toast.makeText(context, "Error: AA-AR-148, make a report.", Toast.LENGTH_LONG)
                            .show()
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

                        add_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0)
                        add_btn.setPadding(20, 0, 10, 0)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
}

@SuppressLint("CheckResult")
fun compressImageUpdate(activity: FragmentActivity?, viewModel: EditViewModel, file: File, imageView: ImageView) {
    Compressor(activity)
        .setQuality(50)
        .setCompressFormat(Bitmap.CompressFormat.WEBP)
        .compressToFileAsFlowable(file)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            val storageDir = activity!!.getDir("imgForNotes", Context.MODE_PRIVATE)

            val success = if (!storageDir.exists()) {
                storageDir.mkdirs()
            } else {
                true
            }
            if (success) {
                // TODO - Remove previous image
                val imageFile = File(storageDir, viewModel.randomImageName)

                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(imageFile)
                    BitmapFactory.decodeFile(file.absolutePath).compress(Bitmap.CompressFormat.JPEG, 50, fos)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        fos?.close()
                        viewModel.changeHasPhotoUpdated(true)

                        // TODO - Fix why can't change more than one time image
                        // TODO - Rounded corners
                        Glide.with(activity)
                            .asBitmap()
                            .load(imageFile)
                            .apply(RequestOptions().transforms(CenterInside()).override(1024, 1024))
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    imageView.setImageBitmap(resource)
                                }
                            })
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
}