package com.mishenka.notbasic.fragments.data

import com.mishenka.notbasic.interfaces.IFragmentData
import com.mishenka.notbasic.interfaces.IPagerData


data class HomeFragmentData(
    val searchField: String?,
    val pagerData: IPagerData?
) : IFragmentData