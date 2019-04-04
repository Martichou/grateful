package me.martichou.be.grateful.utils

import android.annotation.TargetApi
import android.os.Build
import com.google.android.material.appbar.AppBarLayout
import me.martichou.be.grateful.databinding.FragmentHomemainBinding

class ToolbarElevationOffsetListener(private val binding: FragmentHomemainBinding) : AppBarLayout.OnOffsetChangedListener {

    private var mTargetElevation: Float = 0.toFloat()

    init {
        mTargetElevation = convertDpToPixel(4.0f, binding.root.context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOffsetChanged(appBarLayout: AppBarLayout, os: Int) {
        var offset = os
        offset = Math.abs(offset)
        mTargetElevation = Math.max(mTargetElevation, appBarLayout.elevation)
        if (offset >= appBarLayout.totalScrollRange - binding.toolbar.height) {
            val flexibleSpace = (appBarLayout.totalScrollRange - offset).toFloat()
            val ratio = 1 - flexibleSpace / binding.toolbar.height
            val elevation = ratio * mTargetElevation
            setToolbarElevation(elevation)
        } else {
            setToolbarElevation(0f)
        }

    }

    private fun setToolbarElevation(targetElevation: Float) {
        binding.appBar.elevation = targetElevation
    }
}