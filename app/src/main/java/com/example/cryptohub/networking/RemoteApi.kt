package com.example.cryptohub.networking

import android.util.Log
import com.example.cryptohub.model.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Holds decoupled logic for all the API calls.
 */


class RemoteApi(private val apiService: RemoteApiService) {

    private val gson = Gson()


    fun getTopCoins(onCoinsReceived: (Result<GetCoinsResponse>) -> Unit) {
        apiService.getTopCoins().enqueue(object : Callback<GetCoinsResponse> {
            override fun onResponse(
                call: Call<GetCoinsResponse>,
                response: Response<GetCoinsResponse>
            ) {
                val data = response.body()
                if (data != null) {
                    onCoinsReceived(Success(data))
                } else {
                    onCoinsReceived(Failure(data))
                    Log.v("null", "getTopCoins is null in onResponse remoteApi")
                }
            }

            override fun onFailure(call: Call<GetCoinsResponse>, t: Throwable) {}

        })
    }


    fun getTopNews(onNewsReceived: (Result<GetNewsResponse>) -> Unit) {

        apiService.getTopNews().enqueue(object : Callback<GetNewsResponse> {
            override fun onResponse(
                call: Call<GetNewsResponse>,
                response: Response<GetNewsResponse>
            ) {
                val data = response.body()
                if (data != null) {
                    onNewsReceived(Success(data))
                } else {
                    onNewsReceived(Failure(data))
                    Log.v("null", "newsOnResponse is null in onResponse remoteApi")
                }
            }

            override fun onFailure(call: Call<GetNewsResponse>, t: Throwable) {}
        })
    }


    fun getExchangeRates(onRateReceived: (Result<GetExchangeResponse>) -> Unit) {
        apiService.getRates(toSymbol = "USD,CAD,EUR,HKD,ISK,PHP,DKK,HUF,CZK,AUD,RON,SEK,IDR,INR,BRL,RUB,HRK,JPY,THB,CHF,SGD,PLN,BGN,CNY,NOK,NZD,ZAR,MXN,GBP,KRW,MYR")
            .enqueue(object : Callback<GetExchangeResponse> {
                override fun onResponse(
                    call: Call<GetExchangeResponse>,
                    response: Response<GetExchangeResponse>
                ) {
                    val data = response.body()
                    if (data != null) {
                        onRateReceived(Success(data))
                    } else {
                        onRateReceived(Failure(data))
                        Log.v("null", "exchangeOnResponse is null in onResponse remoteApi")
                    }
                }

                override fun onFailure(call: Call<GetExchangeResponse>, t: Throwable) {}
            })
    }


    fun getChartData(
        period: String,
        symbol: String,
        onChartDataReceived: (List<GetChartDataResponse.Data.Data>, GetChartDataResponse.Data.Data?) -> Unit
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
            .enqueue(object : Callback<GetChartDataResponse> {

                override fun onResponse(
                    call: Call<GetChartDataResponse>,
                    response: Response<GetChartDataResponse>
                ) {
                    val data = response.body()
                    val dataList = data?.data?.data
                    val baseLine = dataList?.maxByOrNull { it.close.toFloat() }

                    if (dataList != null) {
                        onChartDataReceived(dataList, baseLine)
                    } else {
                        Log.v("null", "chartDataOnResponse is null in onResponse remoteApi")
                    }
                }

                override fun onFailure(call: Call<GetChartDataResponse>, t: Throwable) {}

            })
    }


}