package com.mishenka.notbasic.utils.recycler

import com.mishenka.notbasic.data.content.ItemType


abstract class FavPagerElement(
    argValue: String,
    val type: ItemType
) : PagerElement(argValue)