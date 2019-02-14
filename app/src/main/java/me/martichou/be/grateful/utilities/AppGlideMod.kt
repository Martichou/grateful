package me.martichou.be.grateful.utilities

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.IOException

@GlideModule
class AppGlideMod : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(File::class.java, BitmapFactory.Options::class.java, BitmapSizeDecoder())
        registry.register(BitmapFactory.Options::class.java, Size2::class.java, OptionsSizeResourceTranscoder())
    }
}

class BitmapSizeDecoder : ResourceDecoder<File, BitmapFactory.Options> {
    @Throws(IOException::class)
    override fun handles(file: File, options: Options): Boolean {
        return true
    }

    override fun decode(file: File, width: Int, height: Int, options: Options): Resource<BitmapFactory.Options>? {
        val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, bmOptions)
        return SimpleResource(bmOptions)
    }
}

class OptionsSizeResourceTranscoder : ResourceTranscoder<BitmapFactory.Options, Size2> {
    override fun transcode(resource: Resource<BitmapFactory.Options>, options: Options): Resource<Size2> {
        val bmOptions = resource.get()
        val size = Size2(bmOptions.outWidth, bmOptions.outHeight)
        return SimpleResource(size)
    }
}

data class Size2(val width: Int, val height: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(width)
        dest.writeInt(height)
    }

    override fun describeContents() = 0

    override fun toString(): String = "$width x $height"

    companion object CREATOR : Parcelable.Creator<Size2> {
        override fun createFromParcel(parcel: Parcel): Size2 {
            return Size2(parcel)
        }

        override fun newArray(size: Int): Array<Size2?> {
            return arrayOfNulls(size)
        }
    }

}