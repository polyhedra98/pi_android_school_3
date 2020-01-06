package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.interfaces.IContentExtras


data class LatLngContentExtras(
    val lat: Double,
    val lng: Double,
    val page: Int
) : IContentExtras