package com.example.cryptohub

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.cryptohub.adapters.ChartAdapter
import com.example.cryptohub.databinding.ActivityCoinBinding
import com.example.cryptohub.fragments.SEND_ABOUT_DATA_TO_COIN_ACTIVITY
import com.example.cryptohub.fragments.SEND_COIN_DATA_TO_COIN_ACTIVITY
import com.example.cryptohub.model.Coin
import com.example.cryptohub.model.CoinAboutItem
import com.example.cryptohub.model.GetChartDataResponse
import com.example.cryptohub.model.Success
import com.example.cryptohub.networking.*
import kotlinx.coroutines.launch

class CoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinBinding
    private lateinit var coin: Coin
    private var coinAbout: CoinAboutItem? = null
    private val remoteApi = App.remoteApi
    private val networkStatusChecker by lazy {
        NetworkChecker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coin = intent.getParcelableExtra(SEND_COIN_DATA_TO_COIN_ACTIVITY)!!

        initUi()
    }

    private fun initUi() {

        initStatisticsUi()
        initChartUi()
        initAboutUi()

        binding.layoutToolbar.toolbarTextView.text = coin.coinName
    }

    private fun initStatisticsUi() {

        binding.layoutStatistics.tvOpenAmount.text = coin.open
        binding.layoutStatistics.tvTodaysHighAmount.text = coin.todaysHigh
        binding.layoutStatistics.tvTodayLowAmount.text = coin.todaysLow
        binding.layoutStatistics.tvChangeTodayAmount.text = coin.todaysChange
        binding.layoutStatistics.tvAlgorithm.text = coin.algorithm
        binding.layoutStatistics.tvTotalVolume.text = coin.totalVolume
        binding.layoutStatistics.tvAvgMarketCapAmount.text = coin.marketCapDisplay
        binding.layoutStatistics.tvSupplyNumber.text = coin.supply

    }

    private fun initAboutUi() {
        val bundle = intent.getBundleExtra("bundle")!!
        coinAbout = bundle.getParcelable(SEND_ABOUT_DATA_TO_COIN_ACTIVITY)

        if (coinAbout != null) {
            binding.layoutAbout.txtWebsite.text = coinAbout!!.coinWebsite
            binding.layoutAbout.txtGithub.text = coinAbout!!.coinGithub
            binding.layoutAbout.txtReddit.text = coinAbout!!.coinReddit
            binding.layoutAbout.txtTwitter.text = "@" + coinAbout!!.coinTwitter
            binding.layoutAbout.txtAboutCoin.text = coinAbout!!.coinDesc


            binding.layoutAbout.txtWebsite.setOnClickListener { openCoinWebsite(coinAbout!!.coinWebsite!!) }
            binding.layoutAbout.txtGithub.setOnClickListener { openCoinWebsite(coinAbout!!.coinGithub!!) }
            binding.layoutAbout.txtReddit.setOnClickListener { openCoinWebsite(coinAbout!!.coinReddit!!) }
            binding.layoutAbout.txtTwitter.setOnClickListener { openCoinWebsite("https://twitter.com/" + coinAbout!!.coinTwitter!!) }

        }
    }

    private fun openCoinWebsite(url: String) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun initChartUi() {
        getChartData()
        binding.layoutChart.txtChartPrice.text = coin.coinPrice
        binding.layoutChart.txtChartChange2.text = coin.changePctToday + "%"
        val change = coin.changePctToday
        if (change.contains("-")) {

            val color = ContextCompat.getColor(
                binding.root.context,
                R.color.colorLoss
            )


            binding.layoutChart.txtChartChange2.setTextColor(color)
            binding.layoutChart.txtChartUpDown.setTextColor(color)
            binding.layoutChart.sparkViewMain.lineColor = color
            binding.layoutChart.txtChartUpDown.text = "▼"

        } else if (change == "0.00") {

            val color = ContextCompat.getColor(
                binding.root.context,
                R.color.quaternaryTextColo
            )
            binding.layoutChart.sparkViewMain.lineColor = color
            binding.layoutChart.txtChartUpDown.text = ""


        } else {

            val color = ContextCompat.getColor(
                binding.root.context,
                R.color.colorGain
            )
            binding.layoutChart.txtChartChange2.setTextColor(color)
            binding.layoutChart.txtChartUpDown.setTextColor(color)
            binding.layoutChart.sparkViewMain.lineColor = color
            binding.layoutChart.txtChartUpDown.text = "▲"
        }

        binding.layoutChart.txtChartChange1.text = " " + coin.todaysChange


        binding.layoutChart.sparkViewMain.setScrubListener {

            if (it != null) {
                binding.layoutChart.txtChartPrice.text =
                    "$ " + (it as GetChartDataResponse.Data.Data).close.toString()
            } else {
                binding.layoutChart.txtChartPrice.text = coin.coinPrice
            }

        }
    }

    private fun getChartData() {

        var period = HOUR
        // how should i launch this coroutine inside an activity?
        lifecycleScope.launch {
            requestAndShowChartData(period)
        }
        binding.layoutChart.radioGroupCoinsDetail.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_12h -> period = HOUR
                R.id.radio_1d -> period = HOURS24
                R.id.radio_1w -> period = WEEK
                R.id.radio_1m -> period = MONTH
                R.id.radio_3m -> period = MONTH3
                R.id.radio_1y -> period = YEAR
                R.id.radio_all -> period = ALL
            }
            lifecycleScope.launch {
                requestAndShowChartData(period)
            }
        }
    }


    private suspend fun requestAndShowChartData(period: String) {
        networkStatusChecker.performIfConnectedToInternet {
            val result = remoteApi.getChartData(period, coin.currencySymbol)
            if (result is Success) {
                val chartAdapter = ChartAdapter(result.data.first,result.data.second?.open.toString())
                binding.layoutChart.sparkViewMain.adapter = chartAdapter
            }
        }
    }
}
