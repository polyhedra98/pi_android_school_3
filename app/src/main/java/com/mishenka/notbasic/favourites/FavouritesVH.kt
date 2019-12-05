package com.mishenka.notbasic.favourites

import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.PhotosViewHolder

class FavouritesVH(
    itemView: View,
    private val onClickListener: (Int) -> Unit,
    private val removeButtonListener: (Int) -> Unit
): PhotosViewHolder(itemView) {

    override fun executeBindings(item: String, position: Int) {

        itemView.findViewById<ImageView>(R.id.item_favourite_picture_iv)?.let { pictureIV ->
            Glide.with(pictureIV)
                .load(item)
                .centerCrop()
                .into(pictureIV)
            pictureIV.setOnClickListener {
                onClickListener(position)
            }
        }

        itemView.findViewById<Button>(R.id.item_favourite_remove_b)?.let { removeB ->
            removeB.setOnClickListener {
                removeButtonListener(position)
            }
        }

    }
}