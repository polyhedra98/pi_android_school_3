package com.mishenka.notbasic.interfaces

interface IPagerData {

    val currentPage: Int

    val lastPage: Int

    val pagerList: List<IPagerElement>

}