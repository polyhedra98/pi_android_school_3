package com.mishenka.notbasic.data.fragment

import com.mishenka.notbasic.interfaces.IFragmentData
import com.mishenka.notbasic.interfaces.IPagerData


data class FavouritesFragmentData(
    val userId: Long?,
    val pagerData: IPagerData?
) : IFragmentData