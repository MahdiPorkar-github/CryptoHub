package com.example.cryptohub.model


import com.google.gson.annotations.SerializedName

data class GetNewsResponse(
    @SerializedName("Data")
    val `data`: List<Data>,
) {
    data class Data(
        @SerializedName("body")
        val body: String,
        @SerializedName("imageurl")
        val imageurl: String,
        @SerializedName("source")
        val source: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("url")
        val url: String
    )
}