package com.mishenka.notbasic.data.source

import com.mishenka.notbasic.data.ApiService
import com.mishenka.notbasic.data.model.OuterClass
import com.mishenka.notbasic.data.model.SearchCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppRepository {

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


    companion object {

        private var INSTANCE: AppRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(AppRepository::class.java) {
                INSTANCE ?: AppRepository().also { INSTANCE = it }
            }

    }

}