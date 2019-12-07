package com.mishenka.notbasic.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.favourites.FavouritesVH
import com.mishenka.notbasic.home.HeaderVH
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.util.PhotosAdapter


//TODO("Would be really nice to simply write provideHeader() once, instead of copying it every time")
class GalleryAdapter(items: List<String>, private val homeVM: HomeVM)
    : PhotosAdapter<FavouritesVH, HeaderVH>(items.toMutableList()) {

    override fun pFactory(parent: ViewGroup) =
        FavouritesVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favourite_picture_card, parent, false),
            homeVM::onGalleryItemClicked,
            this::removeGalItem
            )


    override fun hFactory(parent: ViewGroup) =
        HeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))


    fun removeGalItem(position: Int) {
        homeVM.requestGalleryDismiss(position)
        removeItem(position)
    }

}