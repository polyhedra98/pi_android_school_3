package com.mishenka.notbasic.data.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_search")
data class FavouriteSearch(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "category") val query: String
)