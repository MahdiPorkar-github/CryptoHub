package com.example.cryptohub.onClickInterfaces

import com.example.cryptohub.model.GetNewsResponse

interface NewsEvents {
    fun onNewsItemClicked(news: GetNewsResponse.Data)
}