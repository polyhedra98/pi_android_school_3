package com.mishenka.notbasic.utils.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mishenka.notbasic.R


class StdAdapter(items: List<String>,
                 private val onClickListener: (String) -> Unit)
    : PhotosAdapter<PhotoVH, HeaderVH>(items.toMutableList()) {

    override val TAG: String = "StdAdapter"


    override fun pFactory(parent: ViewGroup) =
        PhotoVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_card, parent, false),
            onClickListener)

    override fun hFactory(parent: ViewGroup) =
        HeaderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))


}