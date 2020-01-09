package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.interfaces.IContentResponse


class FavContentResponse(
    val favouriteItemsList: List<String>,
    val favouriteItemsInfo: List<FavItemType>,
    val totalPages: Int
) : IContentResponse