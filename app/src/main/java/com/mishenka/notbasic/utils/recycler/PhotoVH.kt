package com.mishenka.notbasic.utils.recycler

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R


class PhotoVH(
    itemView: View,
    private val onClickListener: (PagerElement) -> Unit
): PhotosViewHolder(itemView) {

    override fun executeBindings(item: PagerElement) {
        itemView.findViewById<ImageView>(R.id.item_picture_iv)?.let { pictureIV ->
            Glide.with(pictureIV)
                .load(item.value)
                .centerCrop()
                .into(pictureIV)
            pictureIV.setOnClickListener {
                onClickListener(item)
            }
        }
    }
}