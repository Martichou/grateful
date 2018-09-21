package me.martichou.be.grateful.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import me.martichou.be.grateful.utilities.GlideApp
import java.io.File

/**
 * Bind the app:imageFromFile="imgName" from xml
 * If image is null or equals to none, set the view
 * as gone to prevent big white space.
 */
// TODO - Try to pass multiple string inside the imageUrl string and split them (color - image)
// TODO - Or just save in image column, either the name of the image or the HEX code and sort them
// TODO - By the presence of the # before or not.

// TODO - Maybe update .into
@BindingAdapter("imageFromFile")
fun imageFromFile(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty() && imageUrl != "none") {
        GlideApp.with(view.context)
            .asBitmap()
            .load(File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
            .apply(RequestOptions().override(1024, 768))
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    view.setImageBitmap(resource)
                }
            })
    }
}


/**
 * Will hide the textview if the string is empty
 */
@BindingAdapter("isGone")
fun isGone(v: TextView, content: String?) {
    if (content.isNullOrEmpty()) {
        v.visibility = View.GONE
    } else {
        v.visibility = View.VISIBLE
    }
}

/**
 * Format raw date to only "25 Sep" by exemple
 */
@SuppressLint("SetTextI18n")
@BindingAdapter("dateToNumberOnly")
fun dateToNumberOnly(v: TextView, content: String?) {
    val splited = content!!.split(" ")
    v.text = splited[2] + " " + splited[1]
}

/**
 * Format raw date to only "12:15" by exemple
 */
@SuppressLint("SetTextI18n")
@BindingAdapter("dateToHours")
fun dateToHours(v: TextView, content: String?) {
    // TODO - PM/AM check
    val splited = content!!.split(" ")[3].split(":")
    v.text = "At " + splited[0] + ":" + splited[1]
}