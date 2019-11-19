package com.mishenka.notbasic.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.util.interfaces.ReplaceableAdapter

class HomeAdapter(
    private var items: List<String> = emptyList()
) : RecyclerView.Adapter<HomeAdapter.PicHolder>(),
    ReplaceableAdapter {

    override fun <T> replaceItems(newItems: List<T>) {
        items = newItems as List<String>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PicHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_picture_card, parent, false))

    override fun getItemCount() =
        items.size

    override fun onBindViewHolder(holder: PicHolder, position: Int) {
        holder.picIV?.let { safePicView ->
            Glide.with(safePicView.context)
                .load(items[position])
                .into(safePicView)
        }
    }

    class PicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picIV: ImageView? = itemView.findViewById(R.id.item_picture_iv)
    }

}