package com.example.cryptohub.networking

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Holds the API calls for the Taskie app.
 */
interface RemoteApiService {

    // get top news
    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query("lang") lang: String = "EN" , @Query("sortOrder") sortOrder : String = "popular"
    ): Call<ResponseBody>


    // get top coins


    // get exchange rates
    @Headers(API_KEY)
    @GET("price")
    fun getRates(
        @Query("fsym") fromSymbol: String = "USD", @Query("tsyms") toSymbol: String
    ): Call<ResponseBody>
}