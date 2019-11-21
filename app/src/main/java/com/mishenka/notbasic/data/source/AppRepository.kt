package com.mishenka.notbasic.data.source

import android.content.Context
import com.mishenka.notbasic.data.ApiService
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.data.model.photo.SearchCallback
import com.mishenka.notbasic.data.model.user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppRepository private constructor(
    private val appDatabase: AppDatabase
) {

    fun getSearchResults(query: String, callback: SearchCallback, page: Int = 1) {
        val apiKey = "d64c48cfef077371e18078e6e3657da5"
        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        val call = service.getSearchList(
            "flickr.photos.search",
            apiKey,
            query,
            page = page
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                callback.onDataNotAvailable(t.localizedMessage ?: "Unexpected error")
            }

            override fun onResponse(
                call: Call<OuterClass?>,
                response: Response<OuterClass?>
            ) {
                callback.onSearchCompleted(response)
            }
        })
    }


    suspend fun insertFavouriteSearch(favouriteSearch: FavouriteSearch) =
        appDatabase.userDao().insertFavSearch(favouriteSearch)


    suspend fun insertFavourite(favourite: Favourite) =
        appDatabase.userDao().insertFav(favourite)


    suspend fun insertFSU(fsu: FavouriteToSearchToUser) =
        appDatabase.userDao().insertFavToSearchToUser(fsu)


    suspend fun insertHistory(history: History) =
        appDatabase.userDao().insertHistory(history)


    suspend fun insertUser(user: User) =
        appDatabase.userDao().insertUser(user)


    suspend fun getHistoryByUserId(userId: Long) =
        appDatabase.userDao().getUserHistory(userId)


    suspend fun getUserIdByUsername(username: String) =
        appDatabase.userDao().getUserIdByUsername(username)


    suspend fun getFSUid(userId: Long, favouriteId: Long, categoryId: Long) =
        appDatabase.userDao().getFavToSearchToUserId(userId, favouriteId, categoryId)


    suspend fun getFavIdByUrl(url: String) =
        appDatabase.userDao().getFavIdByUrl(url)


    suspend fun getFavSearchIdByCategory(category: String) =
        appDatabase.userDao().getFavSearchIdByCategory(category)


    suspend fun deleteFSUbyIds(userId: Long, favId: Long, categoryId: Long) =
        appDatabase.userDao().deleteFavToSearchToUserByIds(userId, favId, categoryId)


    companion object {

        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(AppRepository::class.java) {
                INSTANCE ?: AppRepository(
                    AppDatabase.getInstance(context)
                ).also { INSTANCE = it }
            }

    }

}