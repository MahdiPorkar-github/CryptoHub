package com.example.cryptohub.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptohub.App
import com.example.cryptohub.R
import com.example.cryptohub.adapters.NewsAdapter
import com.example.cryptohub.databinding.FragmentNewsBinding
import com.example.cryptohub.model.News
import com.example.cryptohub.networking.NetworkChecker
import com.example.cryptohub.onClickInterfaces.NewsEvents

class NewsFragment : Fragment(R.layout.fragment_news),NewsEvents {

    private val adapter by lazy {
        NewsAdapter(this)
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
        initListeners()


    }

    private fun initListeners() {

        binding.newsSwipeRefresh.setOnRefreshListener {

            initUi()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.newsSwipeRefresh.isRefreshing = false
            }, 1500)

        }

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

    override fun onNewsItemClicked(news: News) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                startActivity(intent)
    }


}