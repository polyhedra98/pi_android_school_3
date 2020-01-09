package com.mishenka.notbasic.utils.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.FavItemType

class FavAdapter(items: MutableList<String>,
                 private var additionalInfo: MutableList<FavItemType>,
                 private val onClickListener: (Int) -> Unit,
                 private val onRemoveListener: (Int) -> Unit)
    : PhotosAdapter<FavouritesVH, CategoryHeaderVH>(items) {

    override val TAG = "FavAdapter"


    override fun pFactory(parent: ViewGroup) =
        FavouritesVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite_picture_card, parent, false),
            onClickListener,
            this::removeFavItem
        )


    override fun hFactory(parent: ViewGroup) =
        CategoryHeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header_category, parent, false)
        )


    override fun getItemViewType(position: Int) =
        additionalInfo[position].value


    override fun getItemCount() =
        items.size


    fun replaceFavItems(newItems: List<String>, newAdditionalInfo: List<FavItemType>) {
        items = newItems.toMutableList()
        additionalInfo = newAdditionalInfo.toMutableList()
        notifyDataSetChanged()
    }


    fun removeFavItem(position: Int) {
        onRemoveListener(position)
        removeItem(position)
    }


}