package com.mishenka.notbasic.utils.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.FavItemType

class FavAdapter(items: MutableList<String>,
                 private var additionalInfo: MutableList<FavItemType>,
                 private val onClickListener: (String, String) -> Unit,
                 private val onRemoveListener: (String, String) -> Unit)
    : PhotosAdapter<FavouritesVH, CategoryHeaderVH>(items) {

    override val TAG = "FavAdapter"


    override fun pFactory(parent: ViewGroup) =
        FavouritesVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite_picture_card, parent, false),
            this::favClickListener,
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


    private fun removeFavItem(position: Int) {
        onRemoveListener(items[position], getCategoryForPosition(position))
        removeItem(position)
    }


    private fun favClickListener(position: Int) {
        onClickListener(items[position], getCategoryForPosition(position))
    }


    private fun getCategoryForPosition(position: Int): String {
        var pos = position
        while (additionalInfo[pos] != FavItemType.CATEGORY_TYPE) {
            pos--
        }
        return items[pos]
    }


}