package com.example.cryptohub.networking

import android.util.Log
import com.example.cryptohub.model.GetExchangeResponse
import com.example.cryptohub.model.NewsData
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Holds decoupled logic for all the API calls.
 */

const val BASE_URL = "https://min-api.cryptocompare.com/data/"
const val BASE_URL_IMAGE = "https://www.cryptocompare.com"
const val API_KEY =
    "authorization: Apikey {a602e84ed1984b5ee13d29608b4a1c03e4c7118cd6bb3c2b9b0c406e72e61edd}"
const val API_NAME = "test"


class RemoteApi(private val apiService: RemoteApiService) {

    private val gson = Gson()


//    fun getTopNews(apiCallback: ApiCallback<NewsData.Data>) {
//
//        apiService.getTopNews().enqueue(object : Callback<NewsData> {
//            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
//                val data = response.body()!!
//               apiCallback.onSuccess(data.data)
//            }
//
//            override fun onFailure(call: Call<NewsData>, t: Throwable) {
//                apiCallback.onError(t.message!!)
//            }
//
//        })
//    }


    fun getExchangeRates(
        onRateReceived: (GetExchangeResponse) -> Unit
    ) {
        apiService.getRates(toSymbol = "USD,CAD,EUR,HKD,ISK,PHP,DKK,HUF,CZK,AUD,RON,SEK,IDR,INR,BRL,RUB,HRK,JPY,THB,CHF,SGD,PLN,BGN,CNY,NOK,NZD,ZAR,MXN,GBP,KRW,MYR").enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val jsonBody = response.body()?.string()
                if (jsonBody == null) {
                    Log.v("null", "message is null in onResponse remoteApi")
                }
                val data = gson.fromJson(jsonBody, GetExchangeResponse::class.java)!!
                onRateReceived(data)
                Log.v("exchangeResponse",data.toString())

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


}