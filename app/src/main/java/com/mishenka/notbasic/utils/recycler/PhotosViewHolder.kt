package com.mishenka.notbasic.utils.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class PhotosViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    abstract fun executeBindings(item: String, position: Int)

}