package com.example.cryptohub.onClickInterfaces

import com.example.cryptohub.model.News


interface NewsEvents {
    fun onNewsItemClicked(news: News)
}