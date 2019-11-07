package com.mishenka.notbasic.data.source

import android.util.Log
import okhttp3.HttpUrl


class AppRepository {

    fun getSearchResults(query: String): String? {
        val api_key = "d64c48cfef077371e18078e6e3657da5"
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("www.flickr.com")
            .addPathSegments("services/rest")
            .addQueryParameter("api_key", api_key)
            .addQueryParameter("format", "gson")
            .addQueryParameter("text", query.toLowerCase())
            .build()

        return url.toString()
    }


    companion object {

        private var INSTANCE: AppRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(AppRepository::class.java) {
                INSTANCE ?: AppRepository().also { INSTANCE = it }
            }

    }

}