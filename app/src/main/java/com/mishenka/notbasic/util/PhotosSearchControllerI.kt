package com.mishenka.notbasic.util

import android.content.Context
import com.mishenka.notbasic.data.model.photo.OuterClass
import retrofit2.Response

interface PhotosSearchControllerI {

    fun changePage(params: SearchParams, pageChange: Int)

    fun continuousSearch(params: SearchParams)

    fun trimResults()

    fun search(params: SearchParams)

    fun processSearchResult(context: Context, response: Response<OuterClass?>, isContinuation: Boolean)

    fun initStartingValues(context: Context)

    fun endlessChanged(newValue: Boolean)

}