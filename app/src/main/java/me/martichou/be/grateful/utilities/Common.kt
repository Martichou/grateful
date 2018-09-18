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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
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
fun compressImage(activity: FragmentActivity?, isAdd: Boolean, viewModel: AddViewModel?, editViewModel: EditViewModel?, file: File, add_btn: Button?, imageView: ImageView?) {
    runOnIoThread {
        val storageDir = activity!!.getDir("imgForNotes", Context.MODE_PRIVATE)
        val success = if (!storageDir.exists()) {
            storageDir.mkdirs()
        } else {
            true
        }
        if (success) {
            val imageFile: File = if(isAdd) {
                File(storageDir, viewModel!!.randomImageName)
            } else {
                File(storageDir, editViewModel!!.randomImageName)
            }
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
                    if (isAdd) {
                        compressNextIfAdd(activity, viewModel!!, add_btn!!)
                    } else {
                        compressNextIfUpdate(activity, editViewModel!!, imageView!!, imageFile)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

fun compressNextIfAdd(activity: FragmentActivity?, viewModel: AddViewModel, add_btn: Button){
    viewModel.changeHasPhoto(true)
    viewModel.changeIsWorking(false)

    activity!!.runOnUiThread {
        add_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0)
        add_btn.setPadding(20, 0, 10, 0)
    }
}

fun compressNextIfUpdate(activity: FragmentActivity?, viewModel: EditViewModel, imageView: ImageView, imageFile: File){
    viewModel.changeHasPhotoUpdated(true)

    activity!!.runOnUiThread {
        Glide.with(activity).asBitmap().load(imageFile)
            .apply(RequestOptions().transforms(CenterInside()).override(1024,1024).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) { imageView.setImageBitmap(resource) }
            })
    }
}