package com.mishenka.notbasic.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R
import com.mishenka.notbasic.general.recycler.PhotosAdapter
import com.mishenka.notbasic.general.recycler.vh.HeaderVH
import com.mishenka.notbasic.general.recycler.vh.PhotoVH
import com.mishenka.notbasic.viewmodels.EventVM


class HomeAdapter(items: List<String>, private val eventVM: EventVM)
    : PhotosAdapter<PhotoVH, HeaderVH>(items.toMutableList()) {

    override fun pFactory(parent: ViewGroup) =
        PhotoVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_card, parent, false),
            eventVM::onResultClicked)

    override fun hFactory(parent: ViewGroup) =
        HeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))


}