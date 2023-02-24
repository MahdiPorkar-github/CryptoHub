package com.example.cryptohub.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptohub.App
import com.example.cryptohub.CoinActivity
import com.example.cryptohub.R
import com.example.cryptohub.adapters.CoinsAdapter
import com.example.cryptohub.databinding.FragmentHomeBinding
import com.example.cryptohub.model.Coin
import com.example.cryptohub.model.CoinAboutData
import com.example.cryptohub.model.CoinAboutItem
import com.example.cryptohub.networking.*
import com.google.gson.Gson

const val SEND_COIN_DATA_TO_COIN_ACTIVITY = "coinData"
const val SEND_ABOUT_DATA_TO_COIN_ACTIVITY = "aboutCoin"

class HomeFragment : Fragment(R.layout.fragment_home), CoinsAdapter.CoinsEvents {

    lateinit var binding: FragmentHomeBinding
    lateinit var localData: ArrayList<Coin>
    private lateinit var aboutCoinsMap: MutableMap<String, CoinAboutItem>
    private val adapter by lazy {
        CoinsAdapter(this)
    }
    private val remoteApi = App.remoteApi

    private val networkStatusChecker by lazy {
        NetworkChecker(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
        getCoinAbout()



    }

    private fun initListeners() {


        binding.homeSwipeRefresh.setOnRefreshListener {

            initUi()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.homeSwipeRefresh.isRefreshing = false
            }, 1500)

        }


        binding.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }

        binding.edtSearchCoin.addTextChangedListener { input ->
            searchCoin(input.toString())
        }

        setRadioBtn()

    }

    private fun searchCoin(editTextInput: String) {

        if (editTextInput.isNotEmpty()) {
            val filteredList = localData.filter { coin ->
                coin.coinName!!.contains(editTextInput)
            }
            adapter.setData(filteredList)
        } else {
            adapter.setData(localData)
        }
    }

    private fun initUi() {

        // set the recyclerView
        binding.layoutWatchlist.recyclerMain.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.layoutWatchlist.recyclerMain.adapter = adapter
        getAllCoins()

    }

    private fun getAllCoins() {

        networkStatusChecker.performIfConnectedToInternet {

            remoteApi.getTopCoins { coinsResponse ->
                val data = arrayListOf<Coin>()
                coinsResponse.data.forEach {

                    data.add(
                        Coin(
                            BASE_URL_IMAGE + it.coinInfo.imageUrl,
                            it.coinInfo.fullName,
                            it.dISPLAY.uSD.pRICE,
                            it.rAW.uSD.cHANGEPCT24HOUR,
                            it.rAW.uSD.mKTCAP,
                            it.dISPLAY.uSD.oPEN24HOUR,
                            it.dISPLAY.uSD.hIGH24HOUR,
                            it.dISPLAY.uSD.lOW24HOUR,
                            it.dISPLAY.uSD.cHANGE24HOUR,
                            it.coinInfo.algorithm,
                            it.dISPLAY.uSD.tOTALVOLUME24H,
                            it.dISPLAY.uSD.sUPPLY,
                            it.dISPLAY.uSD.mKTCAP,
                            it.coinInfo.name,
                            it.dISPLAY.uSD.cHANGEPCT24HOUR
                        )
                    )
                }
                localData = data.clone() as ArrayList<Coin>
                adapter.setData(data)
            }
        }
    }

    private fun getCoinAbout() {

        val fileInString = readFromLocalJson()
        val gson = Gson()
        val aboutCoins = gson.fromJson(fileInString, CoinAboutData::class.java)

        aboutCoinsMap = mutableMapOf()
        aboutCoins.forEach {
            aboutCoinsMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.github,
                it.info.twt,
                it.info.desc,
                it.info.reddit
            )
        }
    }

    private fun readFromLocalJson(): String? {
        return context?.applicationContext?.assets?.open("currencyinfo.json")?.bufferedReader()
            .use { it?.readText() }
    }

    override fun onCoinItemClicked(coin: Coin) {

        val intent = Intent(activity, CoinActivity::class.java)
        intent.putExtra(SEND_COIN_DATA_TO_COIN_ACTIVITY, coin)

        val bundle = Bundle()
        bundle.putParcelable(SEND_ABOUT_DATA_TO_COIN_ACTIVITY,aboutCoinsMap[coin.currencySymbol])
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }


    private fun setRadioBtn() {

        binding.radioFilters.setOnCheckedChangeListener { _, checkedId ->

            when(checkedId) {

                R.id.radio_all -> {adapter.setData(localData)}
                R.id.radio_Gainers -> {
                    val gainersList = localData.filter {
                        !it.changePctToday.contains("-") && it.changePctToday != "0.00"
                    }
                    adapter.setData(gainersList)
                }
                R.id.radio_losers -> {

                    val losersList = localData.filter {
                        it.changePctToday.contains("-")
                    }
                    adapter.setData(losersList)
                }
            }
        }
    }

}