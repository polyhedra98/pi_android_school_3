package com.mishenka.notbasic.data.model.user

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.mishenka.notbasic.util.DateConverter
import java.util.*

@TypeConverters(DateConverter::class)

data class HistorySelectItem(
    @ColumnInfo(name = "history_item") val historyItem: String,
    @ColumnInfo(name = "time_stamp") val timeStamp: Date?
)