package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.interfaces.IContentResponse


class LatLngContentResponse(
    val lat: Double,
    val lng: Double,
    val response: OuterClass
) : IContentResponse