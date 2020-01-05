package com.mishenka.notbasic.interfaces

interface IPagerHost {

    fun pagerDataChanged(newData: IPagerData)

    fun pageChangeRequested(newPage: Int)

    fun requestSetup()

}