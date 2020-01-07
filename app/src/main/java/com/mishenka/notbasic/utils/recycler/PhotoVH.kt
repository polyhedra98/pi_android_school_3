package com.mishenka.notbasic.utils.recycler

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R


class PhotoVH(
    itemView: View,
    private val onClickListener: (String) -> Unit
): PhotosViewHolder(itemView) {

    override fun executeBindings(item: String, position: Int) {
        itemView.findViewById<ImageView>(R.id.item_picture_iv)?.let { pictureIV ->
            Glide.with(pictureIV)
                .load(item)
                .centerCrop()
                .into(pictureIV)
            pictureIV.setOnClickListener {
                onClickListener(item)
            }
        }
    }
}