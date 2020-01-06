package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.ApiService
import com.mishenka.notbasic.data.content.LatLngContentExtras
import com.mishenka.notbasic.data.content.LatLngContentResponse
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

class LatLngContentResolver : IContentResolver {

    private val TAG = "LocContentResolver"

    private val apiKey: String = "d64c48cfef077371e18078e6e3657da5"


    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {

        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)

        val ext = (extras as LatLngContentExtras)

        val call = service.getMapSearchList(
            method = "flickr.photos.search",
            apiKey = apiKey,
            lat = "%.6f".format(ext.lat),
            lon = "%.6f".format(ext.lng),
            page = ext.page
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                Log.i("NYA_$TAG", "Error fetching data. ${t.localizedMessage}")
                //TODO("Change to 'proper' error return")
                observable.value = null
            }

            override fun onResponse(call: Call<OuterClass?>, response: Response<OuterClass?>) {
                val body = response.body()
                if (body != null) {
                    observable.value = LatLngContentResponse(ext.lat, ext.lng ,body)
                } else {
                    Log.i("NYA_$TAG", "Error fetching data. Response body is null.")
                    //TODO("Change to 'proper' error return")
                    observable.value = null
                }
            }
        })

    }

}