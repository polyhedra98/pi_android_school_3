package com.mishenka.notbasic.data.model.user

import androidx.room.*
import com.mishenka.notbasic.util.DateConverter
import java.util.*

@Entity(
    tableName = "history",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["user_id"])]
)

@TypeConverters(DateConverter::class)

data class History(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "history_item") val historyItem: String,
    @ColumnInfo(name = "time_stamp") val timeStamp: Date?
)