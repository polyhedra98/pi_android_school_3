package com.mishenka.notbasic.utils.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R


class StdAdapter(argItems: List<PagerElement>,
                 private val onClickListener: (PagerElement) -> Unit,
                 private val onRemoveListener: (PagerElement) -> Unit)
    : ResponsiveAdapter<PhotosViewHolder>(argItems) {

    override val TAG: String = "StdAdapter"


    private val TYPE_HEADER = 1

    private val TYPE_PHOTO = 2


    private fun pFactory(parent: ViewGroup) =
        PhotoVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_card, parent, false),
            onClickListener)


    private fun hFactory(parent: ViewGroup) =
        HeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))



    override fun replaceItems(newItems: List<PagerElement>) {
        super.replaceItems(newItems)

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


    override fun removeItem(position: Int) {
        super.removeItem(position)

        onRemoveListener(items[position])
        items.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun addItem(newItem: PagerElement) {
        super.addItem(newItem)

        items.add(newItem)
        notifyItemInserted(items.size)
    }


    override fun replaceHeader(newHeader: PagerElement) {
        super.replaceHeader(newHeader)

        items[0] = newHeader
        notifyItemChanged(0)
    }


    override fun getItemCount() = items.size


    override fun getItemViewType(position: Int) =
        if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_PHOTO
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == TYPE_HEADER) {
            hFactory(parent)
        } else {
            pFactory(parent)
        }


    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.executeBindings(items[position])
    }

}