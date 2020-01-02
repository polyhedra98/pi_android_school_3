package com.mishenka.notbasic.managers.content

import com.mishenka.notbasic.data.models.StdSearchExtras
import com.mishenka.notbasic.data.models.StdSearchResponse
import com.mishenka.notbasic.data.models.photo.OuterClass
import com.mishenka.notbasic.interfaces.IApiService
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IRequestData
import com.mishenka.notbasic.interfaces.IResponseCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ContentResolverStd : IContentResolver {

    private val TAG = "StandardSearchResolver"

    override val conventionalName = TAG


    private val apiKey = "d64c48cfef077371e18078e6e3657da5"

    private val perPage = 5


    override fun fetchData(dataRequest: IRequestData, callback: IResponseCallback) {
        //TODO("Validate")
        val extras = (dataRequest.extras as StdSearchExtras)
        val query = extras.searchQuery
        val page = extras.page

        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(IApiService::class.java)
        val call = service.getSearchList(
            method = "flickr.photos.search",
            apiKey = apiKey,
            text = query!!,
            page = page,
            per_page = perPage
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                callback.onDataNotAvailable(t.localizedMessage ?: "Unexpected error")
            }

            override fun onResponse(
                call: Call<OuterClass?>,
                response: Response<OuterClass?>
            ) {
                callback.onSuccess(StdSearchResponse(response.body()))
            }
        })
    }

}