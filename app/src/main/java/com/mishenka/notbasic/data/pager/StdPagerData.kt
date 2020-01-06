package com.mishenka.notbasic.data.pager

import com.mishenka.notbasic.interfaces.IPagerData

abstract class StdPagerData : IPagerData {
    abstract val query: String
}