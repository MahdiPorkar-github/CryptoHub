package com.example.cryptohub.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Coin(
    val coinImg: String?,
    val coinName: String?,
    val coinPrice: String,
    val change: Double,
    val marketCap: Double
) : Parcelable