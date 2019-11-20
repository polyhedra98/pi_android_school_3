package com.mishenka.notbasic.data.model.user

import androidx.room.TypeConverters
import com.mishenka.notbasic.util.DateConverter
import java.util.*

@TypeConverters(DateConverter::class)

data class HistorySelectItem(
    val historyItem: String,
    val timeStamp: Date?
)