package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.interfaces.IContentResponse


class GalleryContentResponse(
    val galleryItemsList: List<String>,
    val totalPages: Int
) : IContentResponse