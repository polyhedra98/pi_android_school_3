package com.mishenka.notbasic.data.model.user

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT id FROM user WHERE username = :username LIMIT 1")
    fun getUserIdByUsername(username: String): Long?

}