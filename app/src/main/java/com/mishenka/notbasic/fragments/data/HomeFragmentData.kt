package com.mishenka.notbasic.fragments.data

import com.mishenka.notbasic.interfaces.IFragmentData


data class HomeFragmentData(
    val searchField: String?,
    val validationError: String?,
    val pagerData: StdPagerData?
) : IFragmentData