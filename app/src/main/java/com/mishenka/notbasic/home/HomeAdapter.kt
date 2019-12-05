package com.mishenka.notbasic.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.PhotosAdapter

class HomeAdapter(items: List<String>, private val homeVM: HomeVM)
    : PhotosAdapter<PhotoVH, HeaderVH>(items.toMutableList()) {

    override fun pFactory(parent: ViewGroup) =
        PhotoVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_picture_card, parent, false),
            homeVM::onResultClicked)

    override fun hFactory(parent: ViewGroup) =
        HeaderVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_picture_header, parent, false))


}