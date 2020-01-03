package com.mishenka.notbasic.general.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class PhotosViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    abstract fun executeBindings(item: String, position: Int)

}