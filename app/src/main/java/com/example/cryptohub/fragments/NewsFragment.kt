package com.example.cryptohub.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptohub.App
import com.example.cryptohub.R
import com.example.cryptohub.adapter.NewsAdapter
import com.example.cryptohub.databinding.FragmentNewsBinding
import com.example.cryptohub.model.GetNewsResponse
import com.example.cryptohub.model.News
import com.example.cryptohub.networking.NetworkChecker

class NewsFragment : Fragment(R.layout.fragment_news) {

    private val adapter by lazy {
        NewsAdapter()
    }
    private val remoteApi = App.remoteApi
    private lateinit var binding: FragmentNewsBinding
    private val networkStatusChecker by lazy {
        NetworkChecker(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()


    }

    private fun initUi() {
        // set the recyclerView
        binding.recyclerMain.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerMain.adapter = adapter
        getAllNews()
    }

    private fun getAllNews() {

        networkStatusChecker.performIfConnectedToInternet {

            remoteApi.getTopNews { newsResponse ->
                val data = arrayListOf<News>()
                newsResponse.data.forEach {
                    data.add(News(it.imageurl,it.title,it.url,it.body,it.source))
                }
                adapter.setData(data)
            }
        }
    }


}