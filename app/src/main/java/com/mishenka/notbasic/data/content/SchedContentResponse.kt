package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.model.photo.network.SchedResSelectItem
import com.mishenka.notbasic.interfaces.IContentResponse


class SchedContentResponse(
    val schedItemsList: List<SchedResSelectItem>,
    val totalPages: Int
) : IContentResponse