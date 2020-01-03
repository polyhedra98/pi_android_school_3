package com.mishenka.notbasic.data.models

import com.mishenka.notbasic.interfaces.IRequestDataExtras

data class StdSearchExtras(
    val searchQuery: String?
) : IRequestDataExtras