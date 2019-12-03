package com.mishenka.notbasic.data.source

import android.content.Context
import android.util.Base64
import android.util.Log
import com.mishenka.notbasic.data.ApiService
import com.mishenka.notbasic.data.model.photo.OuterClass
import com.mishenka.notbasic.data.model.photo.SearchCallback
import com.mishenka.notbasic.data.model.user.*
import com.mishenka.notbasic.util.Constants.PER_PAGE
import com.mishenka.notbasic.util.ResponseCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap


class AppRepository private constructor(
    private val appDatabase: AppDatabase
) {

    private val apiKey = "d64c48cfef077371e18078e6e3657da5"
    private val secretKey = "d3569413296ebecf"
    private val authUrl = "https://www.flickr.com/auth-72157711681300693"
    private val callbackUrl = "http%3A%2F%2Fwww.example.com"

    fun getSearchResults(query: String, callback: SearchCallback, isContinuation: Boolean, page: Int = 1) {
        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        val call = service.getSearchList(
            "flickr.photos.search",
            apiKey,
            query,
            page = page
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                callback.onDataNotAvailable(t.localizedMessage ?: "Unexpected error")
            }

            override fun onResponse(
                call: Call<OuterClass?>,
                response: Response<OuterClass?>
            ) {
                callback.onSearchCompleted(response, isContinuation)
            }
        })
    }


    fun getMapSearchResults(lat: String, lng: String,
                            callback: SearchCallback, isContinuation: Boolean = false, page: Int = 1) {
        //val time = Date().time
        //val signature = getMapSearchSignature(lat, lng, page, oauthToken, time)


        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        val call = service.getMapSearchList(
            apiKey = apiKey,
            lat = "%.6f".format(lat.toDouble()),
            lon = "%.6f".format(lng.toDouble()),
            method = "flickr.photos.search",
            page = page
        )
        call.enqueue(object : Callback<OuterClass?> {
            override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                callback.onDataNotAvailable(t.localizedMessage ?: "Unexpected error")
            }

            override fun onResponse(call: Call<OuterClass?>, response: Response<OuterClass?>) {
                callback.onSearchCompleted(response, isContinuation)
            }
        })
    }


    suspend fun getFavourites(userId: Long) =
        appDatabase.userDao().getCategoriesAndFavsForUser(userId)


    suspend fun insertFavouriteSearch(favouriteSearch: FavouriteSearch) =
        appDatabase.userDao().insertFavSearch(favouriteSearch)


    suspend fun insertFavourite(favourite: Favourite) =
        appDatabase.userDao().insertFav(favourite)


    suspend fun insertFSU(fsu: FavouriteToSearchToUser) =
        appDatabase.userDao().insertFavToSearchToUser(fsu)


    suspend fun insertHistory(history: History) =
        appDatabase.userDao().insertHistory(history)


    suspend fun insertUser(user: User) =
        appDatabase.userDao().insertUser(user)


    suspend fun getHistoryByUserId(userId: Long) =
        appDatabase.userDao().getUserHistory(userId)


    suspend fun getUserIdByUsername(username: String) =
        appDatabase.userDao().getUserIdByUsername(username)


    suspend fun getFSUid(userId: Long, favouriteId: Long, categoryId: Long) =
        appDatabase.userDao().getFavToSearchToUserId(userId, favouriteId, categoryId)


    suspend fun getFavIdByUrl(url: String) =
        appDatabase.userDao().getFavIdByUrl(url)


    suspend fun getFavSearchIdByCategory(category: String) =
        appDatabase.userDao().getFavSearchIdByCategory(category)


    suspend fun deleteFSUbyIds(userId: Long, favId: Long, categoryId: Long) =
        appDatabase.userDao().deleteFavToSearchToUserByIds(userId, favId, categoryId)


    private fun getMapSearchSignature(lat: String, lng: String, page: Int,
                                      oauthToken: String, time: Long): String {
        val requestUrl = "https://www.flickr.com/services/rest"
        val apiKeyParameter = "api_key=$apiKey"
        val formatParameter = "format=json"
        val latParameter = "lat=%.6f".format(lat.toDouble())
        val lonParameter = "lon=%.6f".format(lng.toDouble())
        val methodParameter = "method=flickr.photos.geo.photosForLocation"
        val nojsonParameter = "nojsoncallback=1"
        //val consumerKeyParameter = "oauth_consumer_key=$apiKey"
        val nonceParameter = "oauth_nonce=$time"
        val oauthSignatureMethodParameter = "oauth_signature_method=HMAC-SHA1"
        val oauthTimestampParameter = "oauth_timestamp=${time / 1000}"
        val oauthTokenParameter = "oauth_token=$oauthToken"
        val oauthVersionParameter = "oauth_version=2.0"
        val pageParameter = "page=$page"
        val perPageParameter = "per_page=$PER_PAGE"

        val signature = getSignature(
            requestUrl,
            listOf(
                apiKeyParameter,
                formatParameter,
                latParameter,
                lonParameter,
                methodParameter,
                nojsonParameter,
                //consumerKeyParameter,
                nonceParameter,
                oauthSignatureMethodParameter,
                oauthTimestampParameter,
                oauthTokenParameter,
                oauthVersionParameter,
                pageParameter,
                perPageParameter
            )
        )
        return signature
    }


    fun getRequestToken(responseCallback: ResponseCallback) {
        val requestUrl = "https://www.flickr.com/services/oauth/request_token"
        val callbackParameter = "oauth_callback=$callbackUrl"
        val consumerKeyParameter = "oauth_consumer_key=$apiKey"
        val nonceParameter = "oauth_nonce=${Date().time}"
        val signatureMethodParameter = "oauth_signature_method=HMAC-SHA1"
        val timestampParameter = "oauth_timestamp=${Date().time / 1000}"
        val versionParameter = "oauth_version=2.0"

        val signature = getSignature(
            requestUrl,
            listOf(
                callbackParameter,
                consumerKeyParameter,
                nonceParameter,
                signatureMethodParameter,
                timestampParameter,
                versionParameter
            )
        )

        val url = "$requestUrl?$callbackParameter&$consumerKeyParameter&" +
                "$nonceParameter&$signatureMethodParameter&$timestampParameter&$versionParameter&" +
                "oauth_signature=$signature"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        //TODO("Change scope")
        GlobalScope.launch {
            val response = client.newCall(request).execute()
            if (response.body() == null) {
                responseCallback.onFailure("Tried to get request token. Response is null.")
            } else {
                val responseMap = parseResponseBody(response.body()!!.string())
                MainScope().launch {
                    responseCallback.onResponseAcquired(responseMap)
                }
            }
        }
    }


    private fun parseResponseBody(responseBody: String): HashMap<String, String> {
        Log.i("NYA", "Parsing $responseBody")
        val returnMap = HashMap<String, String>()
        responseBody.split("&")
            .forEach {
                val index = it.indexOf("=")
                if (index != -1) {
                    returnMap[it.substring(0.until(index))] = it.substring(index + 1)
                } else {
                    Log.i("NYA", "Invalid response. Error parsing $it")
                    return@forEach
                }
            }

        return returnMap
    }


    private fun getSignature(url: String, params: List<String>): String {
        val part1 = "GET"
        val part2 = oauthEncode(url)
        val part3 = oauthEncodeParams(params)
        val baseString = "$part1&$part2&$part3"
        return hashSignature("$secretKey&", baseString)
    }


    private fun oauthEncode(stringToEncode: String): String {
        Log.i("NYA", "Encoding: $stringToEncode")
        val encodedValue = URLEncoder.encode(stringToEncode, "UTF-8")
            .replace("\\*", "%2A")
            .replace("\\+", "%20")
            .replace("%7E", "~")
        Log.i("NYA", "Encoded: $encodedValue")
        return encodedValue
    }


    private fun oauthEncodeParams(params: List<String>): String {
        val builder = StringBuilder()
        for (param in params) {
            builder.append("${param}&")
        }
        builder.deleteCharAt(builder.length - 1)
        return oauthEncode(builder.toString())
    }


    private fun hashSignature(key: String, data: String): String {
        val hashAlgorithm = "HmacSHA1"
        val keySpec = SecretKeySpec(key.toByteArray(), hashAlgorithm)

        val macInstance = Mac.getInstance(hashAlgorithm)
        macInstance.init(keySpec)

        val signedBytes = macInstance.doFinal(data.toByteArray())
        return Base64.encodeToString(signedBytes, Base64.DEFAULT)
    }


    companion object {

        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(AppRepository::class.java) {
                INSTANCE ?: AppRepository(
                    AppDatabase.getInstance(context)
                ).also { INSTANCE = it }
            }

    }

}