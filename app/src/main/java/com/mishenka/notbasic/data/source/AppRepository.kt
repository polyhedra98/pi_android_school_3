package com.mishenka.notbasic.data.source

import android.util.Log

class AppRepository {

    fun getSearchResults(query: String?): String? {
        return "Not yet implemented results for $query query"
    }


    companion object {

        private var INSTANCE: AppRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(AppRepository::class.java) {
                INSTANCE ?: AppRepository().also { INSTANCE = it }
            }

    }

}