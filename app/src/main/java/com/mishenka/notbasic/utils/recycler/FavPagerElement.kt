package com.mishenka.notbasic.utils.recycler

import com.mishenka.notbasic.data.content.FavItemType


abstract class FavPagerElement(
    argValue: String,
    val type: FavItemType
) : PagerElement(argValue)