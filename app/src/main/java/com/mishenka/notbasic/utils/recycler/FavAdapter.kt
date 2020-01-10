package com.mishenka.notbasic.utils.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.ItemType

class FavAdapter(argItems: List<PagerElement>,
                 private val onClickListener: (String, String) -> Unit,
                 private val onRemoveListener: (String, String) -> Unit)
    : ResponsiveHeaderlessAdapter<PhotosViewHolder>(argItems) {

    override val TAG = "FavAdapter"


    private fun pFactory(parent: ViewGroup) =
        FavouritesVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite_picture_card, parent, false),
            this::localClickListener,
            this::localRemoveListener
        )


    private fun hFactory(parent: ViewGroup) =
        CategoryHeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header_category, parent, false)
        )


    override fun replaceItems(newItems: List<PagerElement>) {
        super.replaceItems(newItems)

        items = newItems.toMutableList()
        notifyDataSetChanged()
    }


    override fun removeItem(position: Int) {
        super.removeItem(position)

        onRemoveListener(items[position].value, getCategoryForPosition(position))
        items.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun addItem(newItem: PagerElement) {
        super.addItem(newItem)

        items.add(newItem)
        notifyItemInserted(items.size)
    }


    override fun getItemCount() = items.size


    override fun getItemViewType(position: Int) =
        (items as MutableList<FavPagerElement>)[position].type.value


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == ItemType.HEADER_TYPE.value) {
            hFactory(parent)
        } else {
            pFactory(parent)
        }


    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.executeBindings(items[position])
    }


    private fun localRemoveListener(pagerElement: PagerElement) {
        removeItem(items.indexOf(pagerElement))
    }


    private fun localClickListener(pagerElement: PagerElement) {
        onClickListener(pagerElement.value, getCategoryForPosition(items.indexOf(pagerElement)))
    }


    private fun getCategoryForPosition(position: Int): String {
        var pos = position
        val localItems = items as MutableList<FavPagerElement>
        while (localItems[pos].type != ItemType.HEADER_TYPE) {
            pos--
        }
        return items[pos].value
    }

}