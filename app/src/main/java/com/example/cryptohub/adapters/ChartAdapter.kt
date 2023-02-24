package com.example.cryptohub.adapters

import com.example.cryptohub.model.GetChartDataResponse
import com.robinhood.spark.SparkAdapter

class ChartAdapter(private val historicalData : List<GetChartDataResponse.Data.Data>,private val baseLine : String?) : SparkAdapter() {

    override fun getCount() = historicalData.size

    override fun getItem(index: Int) = historicalData[index]

    override fun getY(index: Int) = historicalData[index].close.toFloat()

    override fun hasBaseLine() = true

    override fun getBaseLine() = baseLine?.toFloat() ?: super.getBaseLine()

}