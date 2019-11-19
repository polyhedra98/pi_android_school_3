package com.mishenka.notbasic.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R

class HomeAdapter(
    private val homeVM: HomeVM
) : RecyclerView.Adapter<HomeAdapter.PicHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PicHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_picture_card, parent, false))

    override fun getItemCount() =
        homeVM.resultsList.value!!.size

    override fun onBindViewHolder(holder: PicHolder, position: Int) {
        holder.picIV?.let { safePicView ->
            Glide.with(safePicView.context)
                .load(homeVM.resultsList.value!![position])
                .into(safePicView)
        }
    }

    class PicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picIV: ImageView? = itemView.findViewById(R.id.item_picture_iv)
    }

}