package me.martichou.be.grateful.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import me.martichou.be.grateful.utilities.GlideApp
import java.io.File

/**
 * Bind the app:imageFromFile="imgName" from xml
 */
@BindingAdapter("imageFromFile", "requestListener", requireAll = false)
fun imageFromFile(view: ImageView, imageUrl: String?, listener: RequestListener<Drawable>?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(view.context)
                .load(File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
                .override(1024, 768)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.1f)
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