package com.mishenka.notbasic.data.model.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT id FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserIdByUsername(username: String): Long?


    @Query("SELECT history_item, time_stamp FROM history WHERE user_id = :userId")
    suspend fun getUserHistory(userId: Long): List<HistorySelectItem>?


    @Query("""
        SELECT category, url
        FROM favourite_to_search_to_user
        INNER JOIN favourite_search
        ON favourite_to_search_to_user.favourite_search_id = favourite_search.id
        INNER JOIN favourite
        ON favourite_to_search_to_user.favourite_id = favourite.id
        WHERE user_id = :userId
    """)
    suspend fun getCategoriesAndFavsForUser(userId: Long): List<CategoryFavSelectItem>?


    @Query("SELECT id FROM favourite_search WHERE category = :category LIMIT 1")
    suspend fun getFavSearchIdByCategory(category: String): Long?


    @Query("SELECT id FROM favourite WHERE url = :url LIMIT 1")
    suspend fun getFavIdByUrl(url: String): Long?


    @Query("""
        SELECT id FROM favourite_to_search_to_user
        WHERE user_id = :userId AND favourite_id = :favouriteId AND favourite_search_id = :categoryId
        LIMIT 1
    """)
    suspend fun getFavToSearchToUserId(userId: Long, favouriteId: Long, categoryId: Long): Long?


    @Insert
    suspend fun insertFav(favourite: Favourite): Long?


    @Insert
    suspend fun insertFavSearch(favouriteSearch: FavouriteSearch): Long?


    @Insert
    suspend fun insertFavToSearchToUser(favouriteToSearchToUser: FavouriteToSearchToUser): Long?


    @Insert
    suspend fun insertHistory(history: History): Long?


    @Insert
    suspend fun insertUser(user: User): Long?


    @Query("""
        DELETE FROM favourite_to_search_to_user
        WHERE user_id = :userId AND favourite_id = :favId AND favourite_search_id = :categoryId
    """)
    suspend fun deleteFavToSearchToUserByIds(userId: Long, favId: Long, categoryId: Long)

}