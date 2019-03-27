package me.martichou.be.grateful.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
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
 * @return the current time
 */
fun dateDefault(): String {
    return Date().time.toString()
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