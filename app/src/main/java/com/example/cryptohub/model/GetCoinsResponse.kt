package com.example.cryptohub.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger

data class GetCoinsResponse(
    @SerializedName("Data") val `data`: List<Data>,
) {

    @Parcelize
    data class Data(

        @SerializedName("CoinInfo") val coinInfo: CoinInfo,
        @SerializedName("DISPLAY") val dISPLAY: DISPLAY,
        @SerializedName("RAW") val rAW: RAW

    ) : Parcelable {

        @Parcelize
        data class CoinInfo(
            @SerializedName("Algorithm") val algorithm: String,
            @SerializedName("FullName") val coinName: String,
            @SerializedName("ImageUrl") val coinImg: String,
            @SerializedName("Name") val currencySymbol: String,
        ) : Parcelable

        @Parcelize
        data class DISPLAY(
            @SerializedName("USD") val uSD: USD
        ) : Parcelable {

            @Parcelize
            data class USD(
                @SerializedName("PRICE") val coinPrice: String,
                @SerializedName("OPEN24HOUR") val open: String,
                @SerializedName("HIGH24HOUR") val todaysHigh: String,
                @SerializedName("LOW24HOUR") val todaysLow: String,
                @SerializedName("CHANGE24HOUR") val todaysChange: String,
                @SerializedName("TOTALVOLUME24H") val totalVolume: String,
                @SerializedName("SUPPLY") val supply: String,
                @SerializedName("MKTCAP") val marketCapDisplay: String,
                @SerializedName("CHANGEPCT24HOUR") val changePctToday: String,

                ) : Parcelable
        }

        @Parcelize
        data class RAW(
            @SerializedName("USD") val uSD: USD
        ) : Parcelable {
            @Parcelize
            data class USD(
                @SerializedName("CHANGEPCT24HOUR") val change: Double,
                @SerializedName("MKTCAP") val marketCap: Double,
            ) : Parcelable
        }
    }

}