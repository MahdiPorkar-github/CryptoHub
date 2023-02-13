package com.example.cryptohub.model

data class Coin(
    val coinImg: String?,
    val coinName: String?,
    val coinPrice: String,
    val change: Double,
    val marketCap: Double
)