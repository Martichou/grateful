package me.martichou.be.grateful.adapters

import android.databinding.BindingAdapter
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import me.martichou.be.grateful.R

/**
 * Bind the app:imageFromFile="imgName" from xml
 * and apply a transition + rounded corners.
 * If image is null or equals to none, set the view
 * as gone to prevent big white space.
 */
@BindingAdapter("imageFromFile")
fun imageFromFile(view: ImageView, imageUrl: String?) {
    // && !imageUrl.equals("none")
    if (!imageUrl.isNullOrEmpty() ) {
        Glide.with(view.context)
            .load(R.drawable.sample)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(25)))
            .into(view)
        Log.i("Image", imageUrl)
    } else {
        view.visibility = View.GONE
        Log.i("Image", "Gone")
    }
}