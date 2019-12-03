package com.mishenka.notbasic.map

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.home.HomeAdapter
import com.mishenka.notbasic.home.HomeVM


class MapAdapter (
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
            HomeAdapter.HeaderHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_picture_header, parent, false)
            )
        } else {
            HomeAdapter.PicHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_picture_card, parent, false)
            )
        }


    override fun getItemCount() =
        homeVM.mapSearchResultsList.value!!.size + 1


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeAdapter.HeaderHolder) {
            holder.headerTV?.text = homeVM.mapResultsField.value
                ?: holder.itemView.context.getString(R.string.initial_empty_results)
        } else if (holder is HomeAdapter.PicHolder) {
            holder.picIV?.let { safePicView ->
                Glide.with(safePicView.context)
                    .load(homeVM.mapSearchResultsList.value!![position - 1])
                    .centerCrop()
                    .into(safePicView)
                safePicView.setOnClickListener {
                    homeVM.onMapResultClicked(homeVM.mapSearchResultsList.value!![position - 1])
                }
            }
        }
    }

}