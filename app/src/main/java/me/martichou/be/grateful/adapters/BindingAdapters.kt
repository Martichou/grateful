package me.martichou.be.grateful.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import me.martichou.be.grateful.R
import me.martichou.be.grateful.utilities.GlideApp
import java.io.File

/**
 * Bind the app:imageFromFile="imgName" from xml
 * and apply a rounded corners.
 * If image is null or equals to none, set the view
 * as gone to prevent big white space.
 */
// TODO - Rounded corners
@BindingAdapter("imageFromFile")
fun imageFromFile(view: ImageView, imageUrl: String?) {
    view.visibility = View.VISIBLE
    if (!imageUrl.isNullOrEmpty() && imageUrl != "none") {
        GlideApp.with(view.context)
            .asBitmap()
            .load(File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
            .apply(RequestOptions().transforms(RoundedCorners(25)).override(1024, 1024))
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    view.setImageBitmap(resource)
                }
            })
    } else {
        view.visibility = View.GONE
    }
}

/**
 * Bind the app:imageFromFileEdit="imgName" from xml
 * and apply rounded corners.
 * If image is null or equals to none, set the view
 * as placeholder.
 */
// TODO - Rounded corners
@BindingAdapter("imageFromFileEdit")
fun imageFromFileEdit(view: ImageView, imageUrl: String?) {
    // TODO - Make another conditional shit
    if (!imageUrl.isNullOrEmpty() && imageUrl != "none") {
        GlideApp.with(view.context)
            .asBitmap()
            .load(File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
            .apply(RequestOptions().transforms(RoundedCorners(25)).override(1024, 1024))
            .into(view)
    } else {
        GlideApp.with(view.context)
            .asBitmap()
            .load(R.drawable.posterization)
            .apply(RequestOptions().transforms(RoundedCorners(25)).override(1024, 1024))
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