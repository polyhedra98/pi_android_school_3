package com.mishenka.notbasic.data.fragment

import com.mishenka.notbasic.data.pager.StdPagerData
import com.mishenka.notbasic.interfaces.IFragmentData


data class HomeFragmentData(
    val searchField: String?,
    val validationError: String?,
    val pagerData: StdPagerData?
) : IFragmentData