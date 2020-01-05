package com.mishenka.notbasic.managers.content

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.ErrorTypes
import com.mishenka.notbasic.data.models.StdSearchExtras
import com.mishenka.notbasic.data.models.StdSearchResponse
import com.mishenka.notbasic.data.models.photo.OuterClass
import com.mishenka.notbasic.interfaces.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ContentResolverStd : IContentResolver {

    private val TAG = "StdSearchResolver"

    override val conventionalName = TAG


    private val apiKey = "d64c48cfef077371e18078e6e3657da5"

    private val perPage = 5


    override fun fetchData(context: Context,
                           dataRequest: IRequestData,
                           fragmentData: IFragmentData?,
                           observableToUpdate: MutableLiveData<Response>) {
        //TODO("Validate")
        val extras = (dataRequest.extras as StdSearchExtras)
        val query = extras.searchQuery
        if (query.isNullOrBlank()) {
            observableToUpdate.value = Response(
                Pair(ErrorTypes.VALIDATION_ERROR, context.getString(R.string.error_blank)),
                null
            )
            return
        } else if (!query.matches(Regex("^[A-Za-z0-9_]*$"))) {
            observableToUpdate.value = Response(
                Pair(ErrorTypes.VALIDATION_ERROR, context.getString(R.string.error_english)),
                null
            )
            return
        } else {
            observableToUpdate.value = Response(
                null,
                null
            )
        }


        val page = (fragmentData as IFragmentPagerData?)?.currentPage ?: 1
        Log.i("NYA_$TAG", "Loading content for query: $query, page: $page")

        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(IApiService::class.java)
        val call = service.getSearchList(
            method = "flickr.photos.search",
            apiKey = apiKey,
            text = query,
            page = page,
            per_page = perPage
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                observableToUpdate.value = Response(
                    Pair(ErrorTypes.RESPONSE_ERROR, t.localizedMessage),
                    null
                )
            }

            override fun onResponse(
                call: Call<OuterClass?>,
                response: retrofit2.Response<OuterClass?>
            ) {
                observableToUpdate.value = Response(
                    null,
                    StdSearchResponse(query, response.body())
                )
            }
        })
    }

}