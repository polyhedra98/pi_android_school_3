package com.mishenka.notbasic.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.util.Constants.TYPE_PHOTO

class SwipeItemTouchHelperCallback(
    private val listener: SwipeListener
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) =
        if (viewHolder.itemViewType == TYPE_PHOTO) {
            makeMovementFlags(0, ItemTouchHelper.START.or(ItemTouchHelper.END))
        } else {
            makeMovementFlags(0, 0)
        }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemDismiss(viewHolder.adapterPosition)
    }
}