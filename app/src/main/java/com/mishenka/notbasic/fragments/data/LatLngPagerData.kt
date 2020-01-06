package com.mishenka.notbasic.fragments.data

import com.mishenka.notbasic.interfaces.IPagerData


abstract class LatLngPagerData : IPagerData {
    abstract val lat: Double
    abstract val lng: Double
}