package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.model.photo.network.OuterClass
import com.mishenka.notbasic.interfaces.IContentResponse

class StdContentResponse(
    val query: String,
    val response: OuterClass
) : IContentResponse