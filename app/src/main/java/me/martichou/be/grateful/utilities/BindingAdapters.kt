package me.martichou.be.grateful.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Bind the app:imageFromFile="imgName" from xml
 */
@BindingAdapter("imageFromFile", "requestListener", requireAll = false)
fun imageFromFile(view: AppCompatImageView, imageUrl: String?, listener: RequestListener<Drawable>?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(view.context)
                .load(File(view.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
                .override(view.measuredWidth, view.measuredHeight)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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

@BindingAdapter("bindIsGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("textIsEmpty")
fun textIsEmpty(v: TextView, content: String?) {
    if (content != null && content.isEmpty()) {
        val arr: Array<String> = arrayOf(
                "\"One day I will find the right words, and they will be simple.\" \n - Jack Kerouac",
                "\"Words can be like X-rays if you use them properly -- they'll go through anything. You read and you're pierced.\" \n - Aldous Huxley",
                "\"Let me live, love, and say it well in good sentences.\" \n - Sylvia Plath",
                "\"I kept always two books in my pocket, one to read, one to write in.\" \n - Robert Louis Stevenson"
        )
        v.text = arr[randomNumber(0, arr.size - 1).toInt()]
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