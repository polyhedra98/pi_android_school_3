package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.interfaces.IContentResponse
import com.mishenka.notbasic.utils.recycler.FavPagerElement


class FavContentResponse(
    val favouriteItemsList: List<FavPagerElement>,
    val totalPages: Int
) : IContentResponse