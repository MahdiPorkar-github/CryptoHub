package com.example.cryptohub.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptohub.App
import com.example.cryptohub.R
import com.example.cryptohub.adapter.CoinsAdapter
import com.example.cryptohub.databinding.FragmentHomeBinding
import com.example.cryptohub.model.Coin
import com.example.cryptohub.model.GetCoinsResponse
import com.example.cryptohub.model.News
import com.example.cryptohub.networking.BASE_URL_IMAGE
import com.example.cryptohub.networking.NetworkChecker

class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var binding: FragmentHomeBinding
    lateinit var localData: ArrayList<Coin>
    private val adapter by lazy {
        CoinsAdapter()
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
    }

    private fun initListeners() {

        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }

        binding.edtSearchCoin.addTextChangedListener { input ->
            searchCoin(input.toString())
        }

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
                            it.rAW.uSD.mKTCAP
                        )
                    )
                }
                localData = data.clone() as ArrayList<Coin>
                adapter.setData(data)
            }
        }
    }

}