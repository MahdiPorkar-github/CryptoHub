package com.example.cryptohub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cryptohub.databinding.ActivityCoinBinding
import com.example.cryptohub.fragments.SEND_DATA_TO_COIN_ACTIVITY
import com.example.cryptohub.model.Coin

class CoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinBinding
    private lateinit var coin : Coin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() {

        initStatisticsUi()
        initChartUi()
        initAboutUi()

        binding.layoutToolbar.toolbarTextView.text = coin.coinName
    }

    private fun initStatisticsUi() {

        coin = intent.getParcelableExtra<Coin>(SEND_DATA_TO_COIN_ACTIVITY)!!

        binding.layoutStatistics.tvOpenAmount.text = coin.open
        binding.layoutStatistics.tvTodaysHighAmount.text = coin.todaysHigh
        binding.layoutStatistics.tvTodayLowAmount.text = coin.todaysLow
        binding.layoutStatistics.tvChangeTodayAmount.text = coin.todaysChange
        binding.layoutStatistics.tvAlgorithm.text = coin.algorithm
        binding.layoutStatistics.tvTotalVolume.text = coin.totalVolume
        binding.layoutStatistics.tvAvgMarketCapAmount.text = coin.marketCapDisplay.toString()
        binding.layoutStatistics.tvSupplyNumber.text = coin.supply




    }

    private fun initAboutUi() {
    }

    private fun initChartUi() {
    }
}
