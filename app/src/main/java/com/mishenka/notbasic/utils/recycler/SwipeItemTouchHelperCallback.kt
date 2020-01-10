package com.mishenka.notbasic.utils.recycler

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.data.content.ItemType
import com.mishenka.notbasic.interfaces.ISwipeListener

class SwipeItemTouchHelperCallback(
    private val listener: ISwipeListener
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) =
        if (viewHolder.itemViewType == ItemType.HEADER_TYPE.value) {
            makeMovementFlags(0, 0)
        } else {
            makeMovementFlags(0, ItemTouchHelper.START.or(ItemTouchHelper.END))
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