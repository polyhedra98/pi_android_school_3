package com.mishenka.notbasic.data.model.user

import androidx.room.*

@Entity(
    tableName = "favourite_to_search_to_user",
    foreignKeys = [
        ForeignKey(
            entity = Favourite::class,
            parentColumns = ["id"],
            childColumns = ["favourite_id"],
            onDelete = ForeignKey.CASCADE
            ),
        ForeignKey(
            entity = FavouriteSearch::class,
            parentColumns = ["id"],
            childColumns = ["favourite_search_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "favourite_id", "favourite_search_id"], unique = true)
    ]
)
data class FavouriteToSearchToUser(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "favourite_id") val favouriteId: Long,
    @ColumnInfo(name = "favourite_search_id") val favouriteSearchId: Long
)