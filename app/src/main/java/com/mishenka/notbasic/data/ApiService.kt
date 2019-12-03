package com.mishenka.notbasic.data

import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.util.Constants.PER_PAGE
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.sql.Timestamp

interface ApiService {


    @GET("services/rest/?")
    fun getSearchList(@Query("method") method: String,
                      @Query("api_key") apiKey: String,
                      @Query("text") text: String,
                      @Query("page") page: Int = 1,
                      @Query("per_page") per_page: Int = PER_PAGE,
                      @Query("format") format: String = "json",
                      @Query("nojsoncallback") nojson: Int = 1
    ): Call<OuterClass?>


    @GET("services/rest/?")
    fun getMapSearchList(@Query("api_key") apiKey: String,
                         @Query("lat") lat: String,
                         @Query("lon") lon: String,
                         @Query("method") method: String,
                         @Query("oauth_consumer_key") oauthConsumerKey: String,
                         @Query("oauth_nonce") oauthNonce: Long,
                         @Query("oauth_signature") signature: String,
                         @Query("oauth_signature_method") signatureMethod: String,
                         @Query("oauth_timestamp") timestamp: Long,
                         @Query("oauth_token") oauthToken: String,
                         @Query("page") page: Int,
                         @Query("oauth_version") oauthVersion: Double = 2.0,
                         @Query("per_page") perPage: Int = PER_PAGE,
                         @Query("format") format: String = "json",
                         @Query("nojsoncallback") nojsoncallback: Int = 1
    ): Call<OuterClass?>

}