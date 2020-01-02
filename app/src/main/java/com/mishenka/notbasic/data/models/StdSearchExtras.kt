package com.mishenka.notbasic.data.models

import com.mishenka.notbasic.interfaces.IRequestDataExtras

data class StdSearchExtras(
    val page: Int,
    val searchQuery: String?
) : IRequestDataExtras