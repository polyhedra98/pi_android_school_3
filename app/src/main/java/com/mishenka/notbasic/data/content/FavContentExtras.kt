package com.mishenka.notbasic.data.content

import com.mishenka.notbasic.data.source.AppDatabase
import com.mishenka.notbasic.interfaces.IContentExtras

data class FavContentExtras(
    val appDatabase: AppDatabase,
    val userId: Long,
    val page: Int
) : IContentExtras