package com.mishenka.notbasic.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.home.HomeVM

class SwipeItemTouchHelperCallback(
    private val adapter: SwipeItemTouchHelperAdapter,
    private val homeVM: HomeVM
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) =
        if (viewHolder.itemViewType == homeVM.TYPE_CARD) {
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
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }
}