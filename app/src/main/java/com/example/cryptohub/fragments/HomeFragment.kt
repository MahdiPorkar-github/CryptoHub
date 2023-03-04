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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptohub.App
import com.example.cryptohub.CoinActivity
import com.example.cryptohub.R
import com.example.cryptohub.adapters.CoinsAdapter
import com.example.cryptohub.databinding.FragmentHomeBinding
import com.example.cryptohub.model.*
import com.example.cryptohub.networking.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SEND_COIN_DATA_TO_COIN_ACTIVITY = "coinData"
const val SEND_ABOUT_DATA_TO_COIN_ACTIVITY = "aboutCoin"

class HomeFragment : Fragment(R.layout.fragment_home), CoinsAdapter.CoinsEvents {

    lateinit var binding: FragmentHomeBinding
    private lateinit var localData: ArrayList<GetCoinsResponse.Data>
    private lateinit var filteredCoins: ArrayList<GetCoinsResponse.Data>
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
            val filteredList = filteredCoins.filter { coin ->
                coin.coinInfo.coinName!!.contains(editTextInput)
            }
            adapter.setData(filteredList)
        } else {
            adapter.setData(filteredCoins)
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
            viewLifecycleOwner.lifecycleScope.launch {
                val result = remoteApi.getTopCoins()
                withContext(Dispatchers.Main) {
                    if (result is Success) {

                        localData = result.data.data.toMutableList() as ArrayList<GetCoinsResponse.Data>
                        filteredCoins = result.data.data.toMutableList() as ArrayList<GetCoinsResponse.Data>
                        adapter.setData(result.data.data)
                    }
                }
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

    override fun onCoinItemClicked(coin: GetCoinsResponse.Data) {

        val intent = Intent(activity, CoinActivity::class.java)
        intent.putExtra(SEND_COIN_DATA_TO_COIN_ACTIVITY, coin)

        val bundle = Bundle()
        bundle.putParcelable(SEND_ABOUT_DATA_TO_COIN_ACTIVITY, aboutCoinsMap[coin.coinInfo.currencySymbol])
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }


    private fun setRadioBtn() {

        binding.radioFilters.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                R.id.radio_all -> {
                    adapter.setData(localData)
                }
                R.id.radio_Gainers -> {
                    val gainersList = localData.filter {
                        it.dISPLAY.uSD.changePctToday
                        !it.dISPLAY.uSD.changePctToday.contains("-") && it.dISPLAY.uSD.changePctToday != "0.00"
                    }
                    filteredCoins = gainersList as ArrayList<GetCoinsResponse.Data>
                    adapter.setData(filteredCoins)
                }
                R.id.radio_losers -> {

                    val losersList = localData.filter {
                        it.dISPLAY.uSD.changePctToday.contains("-")
                    }
                    filteredCoins = losersList as ArrayList<GetCoinsResponse.Data>
                    adapter.setData(filteredCoins)
                }
            }
        }
    }

}