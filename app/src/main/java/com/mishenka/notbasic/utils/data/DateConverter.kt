package com.mishenka.notbasic.utils.data

import androidx.room.TypeConverter
import java.util.*

object DateConverter {

    @TypeConverter
    @JvmStatic fun toDate(dateLong: Long?) =
        if (dateLong == null) {
            null
        } else {
            Date(dateLong)
        }

    @TypeConverter
    @JvmStatic fun fromData(date: Date?) =
        date?.time

}