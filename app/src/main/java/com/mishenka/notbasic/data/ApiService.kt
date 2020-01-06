package com.mishenka.notbasic.data

import com.mishenka.notbasic.data.model.photo.OuterClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //TODO("Change per_page value.")
    @GET("services/rest/?")
    fun getSearchList(@Query("method") method: String,
                      @Query("api_key") apiKey: String,
                      @Query("text") text: String,
                      @Query("page") page: Int = 1,
                      @Query("per_page") per_page: Int = 5,
                      @Query("format") format: String = "json",
                      @Query("nojsoncallback") nojson: Int = 1
    ): Call<OuterClass?>

}