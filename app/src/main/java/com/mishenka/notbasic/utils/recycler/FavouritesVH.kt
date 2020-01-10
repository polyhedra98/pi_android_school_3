package com.mishenka.notbasic.utils.recycler

import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R

class FavouritesVH(
    itemView: View,
    private val onClickListener: (PagerElement) -> Unit,
    private val removeButtonListener: (PagerElement) -> Unit
): PhotosViewHolder(itemView) {

    override fun executeBindings(item: PagerElement) {

        itemView.findViewById<ImageView>(R.id.item_favourite_picture_iv)?.let { pictureIV ->
            Glide.with(pictureIV)
                .load(item.value)
                .centerCrop()
                .into(pictureIV)
            pictureIV.setOnClickListener {
                onClickListener(item)
            }
        }

        itemView.findViewById<Button>(R.id.item_favourite_remove_b)?.let { removeB ->
            removeB.setOnClickListener {
                removeButtonListener(item)
            }
        }

    }
}