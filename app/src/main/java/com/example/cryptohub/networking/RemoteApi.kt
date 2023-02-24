package com.example.cryptohub.networking

import android.util.Log
import com.example.cryptohub.model.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Holds decoupled logic for all the API calls.
 */


class RemoteApi(private val apiService: RemoteApiService) {

    private val gson = Gson()


    fun getTopCoins(onCoinsReceived: (GetCoinsResponse) -> Unit) {
        apiService.getTopCoins().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val jsonBody = response.body()?.string()
                if (jsonBody == null) {
                    Log.v("null", "coinsOnResponse is null in onResponse remoteApi")
                }
                val data = gson.fromJson(jsonBody, GetCoinsResponse::class.java)
                onCoinsReceived(data)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
    }


    fun getTopNews(onNewsReceived: (GetNewsResponse) -> Unit) {

        apiService.getTopNews().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val jsonBody = response.body()?.string()
                if (jsonBody == null) {
                    Log.v("null", "newsOnResponse is null in onResponse remoteApi")
                }

                val data = gson.fromJson(jsonBody, GetNewsResponse::class.java)!!

                onNewsReceived(data)

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


    fun getExchangeRates(onRateReceived: (GetExchangeResponse) -> Unit) {
        apiService.getRates(toSymbol = "USD,CAD,EUR,HKD,ISK,PHP,DKK,HUF,CZK,AUD,RON,SEK,IDR,INR,BRL,RUB,HRK,JPY,THB,CHF,SGD,PLN,BGN,CNY,NOK,NZD,ZAR,MXN,GBP,KRW,MYR")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val jsonBody = response.body()?.string()
                    if (jsonBody == null) {
                        Log.v("null", "exchangeOnResponse is null in onResponse remoteApi")
                    }
                    val data = gson.fromJson(jsonBody, GetExchangeResponse::class.java)!!
                    onRateReceived(data)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }


    fun getChartData(
        period: String,
        symbol: String,
        onChartDataReceived: (List<GetChartDataResponse.Data.Data>,GetChartDataResponse.Data.Data?) -> Unit
    ) {

        var histoPeriod = ""
        var limit = 30
        var aggregate = 1

        when (period) {

            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }

            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }

            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }

            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }

            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6
            }

            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }

            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }

        }

        apiService.getChartData(histoPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val jsonBody = response.body()?.string()
                    if (jsonBody == null) {
                        Log.v("null", "chartDataOnResponse is null in onResponse remoteApi")
                    }
                    val data = gson.fromJson(jsonBody, GetChartDataResponse::class.java)!!

                    val dataList = data.data.data
                    val baseLine = dataList.maxByOrNull { it.close.toFloat() }

                    onChartDataReceived(dataList,baseLine)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }


}