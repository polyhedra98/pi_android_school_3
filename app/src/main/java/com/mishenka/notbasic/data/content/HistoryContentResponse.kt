package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.model.user.HistorySelectItem
import com.mishenka.notbasic.interfaces.IContentResponse

class HistoryContentResponse(
    val historyData: List<HistorySelectItem>
) : IContentResponse