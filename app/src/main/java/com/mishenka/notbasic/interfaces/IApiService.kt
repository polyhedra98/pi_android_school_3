package com.mishenka.notbasic.interfaces

import com.mishenka.notbasic.data.models.photo.OuterClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService {


    @GET("services/rest/?")
    fun getSearchList(@Query("method") method: String,
                      @Query("api_key") apiKey: String,
                      @Query("text") text: String,
                      @Query("per_page") per_page: Int,
                      @Query("page") page: Int = 1,
                      @Query("format") format: String = "json",
                      @Query("nojsoncallback") nojson: Int = 1
    ): Call<OuterClass?>


}