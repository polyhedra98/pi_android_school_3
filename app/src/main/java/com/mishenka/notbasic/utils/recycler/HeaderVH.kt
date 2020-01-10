package com.mishenka.notbasic.utils.recycler

import android.view.View
import android.widget.TextView
import com.mishenka.notbasic.R


class HeaderVH(itemView: View) : PhotosViewHolder(itemView) {

    override fun executeBindings(item: PagerElement) {
        itemView.findViewById<TextView>(R.id.item_picture_header_tv)?.let { headerTV ->
            headerTV.text = item.value
        }
    }
}