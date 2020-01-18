package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.source.AppDatabase
import com.mishenka.notbasic.interfaces.IContentExtras


data class SchedContentExtras(
    val appDatabase: AppDatabase,
    val page: Int
) : IContentExtras