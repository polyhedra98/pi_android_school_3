package com.mishenka.notbasic.data.pager

import com.mishenka.notbasic.data.content.FavItemType
import com.mishenka.notbasic.interfaces.IPagerData

abstract class FavPagerData : IPagerData {
    abstract val infoList: List<FavItemType>
}