package me.martichou.be.grateful.adapters

import android.content.Context
import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * Bind the app:imageFromFile="imgName" from xml
 * and apply a transition + rounded corners.
 * If image is null or equals to none, set the view
 * as gone to prevent big white space.
 */
@BindingAdapter("imageFromFile")
fun imageFromFile(view: ImageView, imageUrl: String?) {
    // TODO - Solve the round corner on showFragment as well as the double call
    view.visibility = View.VISIBLE
    if (!imageUrl.isNullOrEmpty() && !imageUrl.equals("none")) {

        val image = File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl)
        Glide.with(view.context)
            .load(image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(25)))
            .into(view)

    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("isGone")
fun isGone(v: TextView, content: String?) {
    if (content.isNullOrEmpty()) {
        v.visibility = View.GONE
    } else {
        v.visibility = View.VISIBLE
    }
}