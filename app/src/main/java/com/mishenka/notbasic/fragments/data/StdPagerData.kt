package com.mishenka.notbasic.fragments.data

import com.mishenka.notbasic.interfaces.IPagerData

abstract class StdPagerData : IPagerData {
    abstract val query: String
}