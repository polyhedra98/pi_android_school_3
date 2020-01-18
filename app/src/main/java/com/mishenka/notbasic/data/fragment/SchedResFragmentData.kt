package com.mishenka.notbasic.data.fragment

import com.mishenka.notbasic.interfaces.IFragmentData
import com.mishenka.notbasic.interfaces.IPagerData


data class SchedResFragmentData(
    val query: String?,
    val startTime: Long?,
    val lastTime: Long?,
    val pagerData: IPagerData?
) : IFragmentData