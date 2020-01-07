package com.mishenka.notbasic.interfaces

import com.mishenka.notbasic.utils.recycler.PhotosAdapter
import com.mishenka.notbasic.utils.recycler.PhotosViewHolder

interface IPager {

    fun setupRecycler(adapter: PhotosAdapter<PhotosViewHolder, PhotosViewHolder>)

    fun updateData(data: IPagerData)

}