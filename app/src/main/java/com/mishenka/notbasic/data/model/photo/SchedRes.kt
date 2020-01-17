package com.mishenka.notbasic.data.model.photo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sched_res")
data class SchedRes(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val url: String,
    val page: Int
)