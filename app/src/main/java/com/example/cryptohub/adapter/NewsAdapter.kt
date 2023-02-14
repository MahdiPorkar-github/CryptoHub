package com.example.cryptohub.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptohub.databinding.ItemRecyclerNewsBinding
import com.example.cryptohub.model.News
import com.example.cryptohub.onClickInterfaces.NewsEvents

class NewsAdapter(private val newsEvents: NewsEvents) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val data : ArrayList<News> = arrayListOf()

    inner class NewsViewHolder(private val binding: ItemRecyclerNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(news: News) {
            Glide.with(binding.root).load(news.imageurl).into(binding.imgItemNews)
            binding.txtNewsSource.text = news.source
            binding.txtNewsTitle.text = news.title
            binding.txtNewsBody.text = news.body
            binding.root.setOnClickListener {
                newsEvents.onNewsItemClicked(news)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =ItemRecyclerNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() =  data.size



    fun setData(data: List<News>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }


}


