package com.mishenka.notbasic.map

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.home.HeaderVH
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.home.PhotoVH
import com.mishenka.notbasic.util.PhotosAdapter

class MapAdapter(items: List<String>, private val homeVM: HomeVM)
    : PhotosAdapter<PhotoVH, HeaderVH>(items.toMutableList()) {

    override fun pFactory(parent: ViewGroup) =
        PhotoVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_card, parent, false),
            homeVM::onMapResultClicked)

    override fun hFactory(parent: ViewGroup) =
        HeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))


}