package com.mishenka.notbasic.interfaces

import com.mishenka.notbasic.utils.recycler.PagerElement

interface IPagerData {

    val currentPage: Int

    val lastPage: Int

    val pagerList: List<PagerElement>

}