package com.mishenka.notbasic.data.model.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favourite",
    indices = [Index(value = ["url"], unique = true)])
data class Favourite(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val url: String
)