package com.example.cryptohub.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Coin(
    val coinImg: String?,
    val coinName: String?,
    val coinPrice: String,
    val change: Double,
    val marketCap: Double,
    val open: String,
    val todaysHigh: String,
    val todaysLow: String,
    val todaysChange: String,
    val algorithm: String,
    val totalVolume: String,
    val supply: String,
    val marketCapDisplay: String,
    val currencySymbol: String,
    val changePctToday : String

) : Parcelable