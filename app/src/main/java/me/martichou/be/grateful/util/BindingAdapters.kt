package me.martichou.be.grateful.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Bind the app:imageFromFile="imgName" from xml
 */
@BindingAdapter("imageFromFile", "imageRequestListener", requireAll = false)
fun AppCompatImageView.imageFromFile(imageUrl: String?, listener: RequestListener<Drawable>?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(this)
                .load(File(this.context.getDir("imgForNotes", Context.MODE_PRIVATE), imageUrl))
                .override(this.measuredWidth, this.measuredHeight)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .listener(listener)
                .dontTransform()
                .into(this)
    }
}

/**
 * Hide textView if empty
 */
@BindingAdapter("isGone")
fun View.isGone(content: String?) {
    if (content.isNullOrEmpty())
        this.visibility = View.GONE
    else
        this.visibility = View.VISIBLE
}

/**
 * Display random quote if no description on the note
 */
@BindingAdapter("textIsEmpty")
fun TextView.textIsEmpty(content: String?) {
    if (content.isNullOrEmpty()) {
        val arr: Array<String> = arrayOf(
                "\"One day I will find the right words, and they will be simple.\" \n- Jack Kerouac",
                "\"Words can be like X-rays if you use them properly -- they'll go through anything. You read and you're pierced.\" \n- Aldous Huxley",
                "\"Let me live, love, and say it well in good sentences.\" \n- Sylvia Plath",
                "\"I kept always two books in my pocket, one to read, one to write in.\" \n- Robert Louis Stevenson"
        )
        this.text = arr[randomNumber(0, arr.size - 1).toInt()]
    }
}

/**
 * Transform dd/MM/yyyy to dd
 */
@BindingAdapter("showDayNbr")
fun AppCompatTextView.showDayNbr(content: String) {
    val parsed = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(content)
    val dayNbr = SimpleDateFormat("dd", Locale.getDefault()).format(parsed)

    Timber.d("Day $dayNbr")
    this.text = dayNbr.toString()
}

/**
 * Transform dd/MM/yyyy to E which is day name like Wed
 */
@SuppressLint("SetTextI18n", "DefaultLocale")
@BindingAdapter("showMonthName")
fun AppCompatTextView.showMonthName(content: String) {
    val parsed = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(content)
    val montName = SimpleDateFormat("MMM", Locale.getDefault()).format(parsed)

    Timber.d("Month $montName")
    this.text = montName.substring(0, 1).capitalize() + montName.dropLast(1).substring(1)
}