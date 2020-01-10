package com.mishenka.notbasic.interfaces

import com.mishenka.notbasic.utils.recycler.PagerElement
import com.mishenka.notbasic.utils.recycler.PhotosViewHolder
import com.mishenka.notbasic.utils.recycler.ResponsiveHeaderlessAdapter

interface IPager {

    fun setupRecycler(adapter: ResponsiveHeaderlessAdapter<PhotosViewHolder>)

    fun updateData(data: IPagerData)

    fun updateHeader(newHeader: PagerElement)

}