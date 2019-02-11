package me.martichou.be.grateful.recyclerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DividerRV internal constructor() : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val itemCount = state.itemCount
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition != 0 && itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.top = 45
        } else {
            outRect.top = 45
        }
        view.clipToOutline = true
    }
}