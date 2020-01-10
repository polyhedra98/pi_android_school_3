package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.interfaces.IContentResponse
import com.mishenka.notbasic.utils.recycler.PagerElement


class GalleryContentResponse(
    val galleryItemsList: List<PagerElement>,
    val currentPage: Int,
    val totalPages: Int
) : IContentResponse