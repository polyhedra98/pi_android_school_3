package com.mishenka.notbasic.data.model.user

import androidx.room.*
import com.mishenka.notbasic.util.DateConverter
import java.util.*

@Entity(
    tableName = "history",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)

@TypeConverters(DateConverter::class)

data class History(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val userId: Long,
    val historyItem: String,
    val timeStamp: Date?
)