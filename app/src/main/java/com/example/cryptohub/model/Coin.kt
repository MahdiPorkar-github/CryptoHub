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
    val open: String? = null,
    val todaysHigh: String? = null,
    val todaysLow: String? = null,
    val todaysChange: String? = null,
    val algorithm: String? = null,
    val totalVolume: String? = null,
    val supply: String? = null,
    val marketCapDisplay: String? = null,

    ) : Parcelable