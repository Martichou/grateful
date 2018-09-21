package me.martichou.be.grateful.utilities

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

@GlideModule
class AppGlideMod : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(requestOptions())
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    private fun requestOptions(): RequestOptions {
        return RequestOptions().signature(ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000))).format(DecodeFormat.PREFER_ARGB_8888)
    }
}