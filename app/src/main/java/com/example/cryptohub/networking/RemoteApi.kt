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


    suspend fun getTopCoins(): Result<GetCoinsResponse> = try {
        val data = apiService.getTopCoins()
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }


    suspend fun getTopNews(): Result<GetNewsResponse> = try {
        val data = apiService.getTopNews()
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }


    suspend fun getExchangeRates() :  Result<GetExchangeResponse> = try {
        val data = apiService.getRates(toSymbol = "USD,CAD,EUR,HKD,ISK,PHP,DKK,HUF,CZK,AUD,RON,SEK,IDR,INR,BRL,RUB,HRK,JPY,THB,CHF,SGD,PLN,BGN,CNY,NOK,NZD,ZAR,MXN,GBP,KRW,MYR")
        Success(data)
    } catch (error : Throwable) {
        Failure(error)
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