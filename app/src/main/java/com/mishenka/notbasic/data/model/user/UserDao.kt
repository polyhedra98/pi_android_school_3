package com.mishenka.notbasic.data.model.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT id FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserIdByUsername(username: String): Long?


    @Query("""
        SELECT search, page, time_stamp
        FROM history
        WHERE user_id = :userId
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getUserHistory(userId: Long, limit: Int, offset: Int): List<HistorySelectItem>?


    @Insert
    suspend fun insertUser(user: User): Long?


    @Insert
    suspend fun insertHistory(history: History): Long?

}