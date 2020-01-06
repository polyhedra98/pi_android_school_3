package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.ApiService
import com.mishenka.notbasic.data.content.StdContentResponse
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StdContentResolver : IContentResolver {

    private val TAG = "StdContentResolver"

    private val apiKey: String = "d64c48cfef077371e18078e6e3657da5"

    private val tempQuery: String = "cactus"
    private val tempPage: Int = 1


    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {

        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)

        //TODO("Extras are not implemented. Change temp query and temp page.")
        val call = service.getSearchList(
            method = "flickr.photos.search",
            apiKey = apiKey,
            text = tempQuery,
            page = tempPage
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                Log.i("NYA_$TAG", "Error fetching data. ${t.localizedMessage}")
                //TODO("Change to 'proper' error return")
                observable.value = null
            }

            override fun onResponse(
                call: Call<OuterClass?>,
                response: Response<OuterClass?>
            ) {
                val body = response.body()
                if (body != null) {
                    observable.value = StdContentResponse(tempQuery, body)
                } else {
                    Log.i("NYA_$TAG", "Error fetching data. Response body is null.")
                    //TODO("Change to 'proper' error return")
                    observable.value = null
                }
            }
        })

        /*
        val fakeData = listOf("FAKE_DATA", "FAKE_DATA", "FAKE_DATA")
        observable.value = StdContentResponse(fakeData)
         */
    }

}