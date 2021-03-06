package com.mishenka.notbasic.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mishenka.notbasic.R
import com.mishenka.notbasic.home.HomeAdapter
import com.mishenka.notbasic.home.HomeVM
import com.mishenka.notbasic.util.SwipeItemTouchHelperAdapter

class FavouriteAdapter(
    private val userId: Long,
    private val homeVM: HomeVM
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SwipeItemTouchHelperAdapter {

    override fun onItemDismiss(position: Int) {
        homeVM.dismissFavourite(userId, position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int) =
        homeVM.favouritesList.value!![position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == homeVM.TYPE_HEADER) {
            HomeAdapter.HeaderHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picture_header, parent, false))
        } else {
            FavouritePicHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_favourite_picture_card, parent, false))
        }


    override fun getItemCount() =
        homeVM.favouritesList.value!!.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeAdapter.HeaderHolder) {
            holder.headerTV?.let { safeTV ->
                with(safeTV) {
                    text = homeVM.favouritesList.value!![position].value
                    textSize = 18f
                }
            }
        } else if (holder is FavouritePicHolder) {
            holder.picIV?.let { safePicView ->
                Glide.with(safePicView.context)
                    .load(homeVM.favouritesList.value!![position].value)
                    .centerCrop()
                    .into(safePicView)
                safePicView.setOnClickListener {
                    val pos = homeVM.getCategoryPosForPosition(position)
                    homeVM.onFavouriteClicked(
                        url = homeVM.favouritesList.value!![position].value,
                        category = homeVM.favouritesList.value!![pos].value
                    )
                }
            }
            holder.removeB?.let { safeRemoveButton ->
                safeRemoveButton.setOnClickListener {
                    homeVM.dismissFavourite(userId, position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    class FavouritePicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picIV: ImageView? = itemView.findViewById(R.id.item_favourite_picture_iv)
        val removeB: Button? = itemView.findViewById(R.id.item_favourite_remove_b)
    }
}