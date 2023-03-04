package com.example.cryptohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptohub.databinding.ItemRecyclerNewsBinding
import com.example.cryptohub.model.GetNewsResponse
import com.example.cryptohub.onClickInterfaces.NewsEvents

class NewsAdapter(private val newsEvents: NewsEvents) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val data : ArrayList<GetNewsResponse.Data> = arrayListOf()

    inner class NewsViewHolder(private val binding: ItemRecyclerNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(news: GetNewsResponse.Data) {
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



    fun setData(data: List<GetNewsResponse.Data>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }


}


