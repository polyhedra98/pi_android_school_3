package com.mishenka.notbasic.data.model.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT id FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserIdByUsername(username: String): Long?

    @Insert
    suspend fun insertUser(user: User)

}