package com.mishenka.notbasic.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R

class HomeAdapter(
    private val homeVM: HomeVM
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 1
    private val TYPE_CARD = 2


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_CARD
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == TYPE_HEADER) {
            HeaderHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))
        } else {
            PicHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_card, parent, false))
        }


    override fun getItemCount() =
        homeVM.resultsList.value!!.size + 1


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderHolder) {
            holder.headerTV?.text = homeVM.resultsField.value
                ?: holder.itemView.context.getString(R.string.initial_empty_results)
        } else if (holder is PicHolder) {
            holder.picIV?.let { safePicView ->
                Glide.with(safePicView.context)
                    .load(homeVM.resultsList.value!![position - 1])
                    .centerCrop()
                    .into(safePicView)
                safePicView.setOnClickListener {
                    homeVM.onResultClicked(homeVM.resultsList.value!![position - 1])
                }
            }
        }
    }


    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTV: TextView? = itemView.findViewById(R.id.item_picture_header_tv)
    }

    class PicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picIV: ImageView? = itemView.findViewById(R.id.item_picture_iv)
    }

}