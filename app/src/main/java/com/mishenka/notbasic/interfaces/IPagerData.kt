package com.mishenka.notbasic.interfaces

interface IPagerData {

    val currentPage: Int

    val lastPage: Int

    //TODO("Change back to IPagerElement")
    val pagerList: List<String>

}