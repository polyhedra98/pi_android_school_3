package com.mishenka.notbasic.data.models

import com.mishenka.notbasic.data.models.photo.OuterClass
import com.mishenka.notbasic.interfaces.IResponseData

data class StdSearchResponse(
    val data: OuterClass?
) : IResponseData