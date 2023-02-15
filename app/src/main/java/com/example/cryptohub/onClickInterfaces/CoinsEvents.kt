package com.example.cryptohub.onClickInterfaces

import com.example.cryptohub.model.Coin

interface CoinsEvents {
    fun onCoinsItemClicked(coin : Coin)
}