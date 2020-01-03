package com.mishenka.notbasic.general.recycler

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class PhotosAdapter <P: PhotosViewHolder, H: PhotosViewHolder> (
    private var items: MutableList<String>
): RecyclerView.Adapter<PhotosViewHolder>() {

    val TAG = "PhotosAdapter"


    abstract fun pFactory(parent: ViewGroup): P

    abstract fun hFactory(parent: ViewGroup): H


    fun replaceItems(newItems: List<String>) {
        Log.i("NYA_$TAG", "Replacing items with $newItems")
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
        Log.i("NYA_$TAG", "Adding items $newItems")
        val oldSize = items.size
        val header = items.first()
        items = mutableListOf(header).apply {
            addAll(newItems)
        }
        notifyItemRangeInserted(oldSize, items.size)
    }


    fun removeItem(position: Int) {
        Log.i("NYA_$TAG", "Removing item at position $position")
        items.removeAt(position)
        notifyItemRemoved(position)
    }


    fun addItem(value: String) {
        Log.i("NYA_$TAG", "Adding item $value")
        items.add(value)
        notifyItemInserted(items.size)
    }


    fun replaceHeader(newHeader: String) {
        items[0] = newHeader
        notifyItemChanged(0)
    }


    override fun getItemViewType(position: Int) =
        if (position == 0) {
            ViewTypes.TYPE_HEADER.value
        } else {
            ViewTypes.TYPE_PHOTO.value
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == ViewTypes.TYPE_PHOTO.value) {
            pFactory(parent)
        } else {
            hFactory(parent)
        }


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.executeBindings(items[position], position)
    }

}