package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.interfaces.IContentExtras

data class StdContentExtras(
    val query: String,
    val page: Int
) : IContentExtras