package com.mishenka.notbasic.data.model.user

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.mishenka.notbasic.utils.data.DateConverter
import java.util.*

@TypeConverters(DateConverter::class)

data class HistorySelectItem(
    @ColumnInfo(name = "search") val search: String,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "time_stamp") val timeStamp: Date?
)