package com.mishenka.notbasic.util

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.util.Constants.TYPE_HEADER
import com.mishenka.notbasic.util.Constants.TYPE_PHOTO

abstract class PhotosAdapter <P: PhotosViewHolder, H: PhotosViewHolder> (
    private var items: MutableList<String>
): RecyclerView.Adapter<PhotosViewHolder>() {

    abstract fun pFactory(parent: ViewGroup): P

    abstract fun hFactory(parent: ViewGroup): H

    //TODO("Use ListDif instead")
    fun replaceItems(newItems: List<String>) {
        Log.i("NYA", "Replacing items")
        val oldSize = items.size
        val header = items.first()
        items = mutableListOf(header).apply {
            addAll(newItems)
        }
        if (items.size == oldSize && oldSize > 1) {
            notifyItemRangeChanged(1, oldSize)
        } else {
            notifyDataSetChanged()
        }
    }


    fun pseudoAddItems(newItems: List<String>) {
        Log.i("NYA", "Adding items")
        val oldSize = items.size
        val header = items.first()
        items = mutableListOf(header).apply {
            addAll(newItems)
        }
        notifyItemRangeInserted(oldSize, items.size)
    }


    fun removeItem(position: Int) {
        Log.i("NYA", "Removing item at position $position")
        items.removeAt(position)
        notifyItemRemoved(position)
    }


    fun replaceHeader(newHeader: String) {
        items[0] = newHeader
        notifyItemChanged(0)
    }


    override fun getItemViewType(position: Int) =
        if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_PHOTO
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == TYPE_PHOTO) {
            pFactory(parent)
        } else {
            hFactory(parent)
        }


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        Log.i("NYA", "(from PhotosAdapter) Binding ViewHolder")
        holder.executeBindings(items[position], position)
    }

}