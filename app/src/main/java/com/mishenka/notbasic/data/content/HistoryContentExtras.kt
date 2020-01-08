package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.source.AppDatabase
import com.mishenka.notbasic.interfaces.IContentExtras


data class HistoryContentExtras(
    val appDatabase: AppDatabase,
    val userId: Long
) : IContentExtras