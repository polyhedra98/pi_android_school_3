package com.mishenka.notbasic.data.fragment

import com.mishenka.notbasic.data.pager.FavPagerData
import com.mishenka.notbasic.interfaces.IFragmentData


data class FavouritesFragmentData(
    val userId: Long?,
    val pagerData: FavPagerData?
) : IFragmentData