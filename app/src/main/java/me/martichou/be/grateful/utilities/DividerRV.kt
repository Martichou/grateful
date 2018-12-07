package me.martichou.be.grateful.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DividerRV internal constructor(private val horizontalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val itemCount = state.itemCount
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == 0) {
            outRect.left = 45
            outRect.right = horizontalSpaceHeight
        } else if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.right = 45
        } else {
            outRect.right = horizontalSpaceHeight
        }
        view.clipToOutline = true
    }

}