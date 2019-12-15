package com.mishenka.notbasic.favourites

import android.view.View
import android.widget.TextView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.PhotosViewHolder

class CategoryHeaderVH(itemView: View) : PhotosViewHolder(itemView) {

    override fun executeBindings(item: String, position: Int) {
        itemView.findViewById<TextView>(R.id.item_picture_header_category_tv)?.let { headerTV ->
            headerTV.text = item
        }
    }
}