package com.mishenka.notbasic.data.model.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT id FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserIdByUsername(username: String): Long?

    @Query("SELECT historyItem, timeStamp FROM history WHERE userId = :userId")
    suspend fun getUserHistory(userId: Long): List<HistorySelectItem>?

    @Insert
    suspend fun insertHistory(history: History)

    @Insert
    suspend fun insertUser(user: User)

}