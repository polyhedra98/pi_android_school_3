package com.mishenka.notbasic.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.util.PhotosAdapter

class FavouritesAdapter(private var items: MutableList<String>,
                        private val homeVM: HomeVM,
                        private var additionalInfo: MutableList<Int>)
    : PhotosAdapter<FavouritesVH, CategoryHeaderVH>(items.toMutableList()) {


    override fun pFactory(parent: ViewGroup) =
        FavouritesVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite_picture_card, parent, false),
            homeVM::onFavouriteClicked,
            this::removeFavItem
            )


    override fun hFactory(parent: ViewGroup) =
        CategoryHeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header_category, parent, false)
        )


    override fun getItemViewType(position: Int) =
        additionalInfo[position]


    override fun getItemCount() =
        items.size


    fun replaceFavItems(newItems: List<String>, newAdditionalInfo: List<Int>) {
        items = newItems.toMutableList()
        additionalInfo = newAdditionalInfo.toMutableList()
        notifyDataSetChanged()
    }


    fun removeFavItem(position: Int) {
        homeVM.requestFavouriteDismiss(position)
        removeItem(position)
    }

}