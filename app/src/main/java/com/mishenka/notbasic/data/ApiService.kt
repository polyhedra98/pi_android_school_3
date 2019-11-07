package com.mishenka.notbasic.data

import com.mishenka.notbasic.data.model.OuterClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("services/rest/?")
    fun getSearchList(@Query("method") method: String,
                      @Query("api_key") apiKey: String,
                      @Query("text") text: String,
                      @Query("page") page: Int = 1,
                      @Query("format") format: String = "json",
                      @Query("nojsoncallback") nojson: Int = 1
    ): Call<OuterClass?>
}