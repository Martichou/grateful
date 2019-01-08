package me.martichou.be.grateful.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Bind the app:imageFromFile="imgName" from xml
 */
@BindingAdapter("imageFromFile", "requestListener", requireAll = false)
fun imageFromFile(view: ImageView, imageUrl: String?, listener: RequestListener<Drawable>?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(view.context)
                .load(File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(view.measuredWidth, view.measuredHeight)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.2f)
                .listener(listener)
                .into(view)
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
 * Set 07 if it was 07/02/2019
 */
@BindingAdapter("getNumberFromDate")
fun getNumberFromDate(v: TextView, content: String?) {
    v.text = content!!.split("/")[0]
}

/**
 * Set lun/mon/...
 */
@BindingAdapter("getNameOfDay")
fun getNameOfDay(v: TextView, content: String?) {
    val sdf = SimpleDateFormat("EE", Locale.getDefault())
    v.text = sdf.format(stringToDate(content)).removeSuffix(".")
}