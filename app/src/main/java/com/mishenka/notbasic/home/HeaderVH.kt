package com.mishenka.notbasic.home

import android.view.View
import android.widget.TextView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.PhotosViewHolder

class HeaderVH(itemView: View) : PhotosViewHolder(itemView) {

    override fun executeBindings(item: String, position: Int) {
        itemView.findViewById<TextView>(R.id.item_picture_header_tv)?.let { headerTV ->
            headerTV.text = item
        }
    }
}