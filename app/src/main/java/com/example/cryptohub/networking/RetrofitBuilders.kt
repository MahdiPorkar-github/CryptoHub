package com.example.cryptohub.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Builds retrofit client providing a base url and other configuration you might need.
 */
fun buildClient(): OkHttpClient =
    OkHttpClient.Builder()
        .build()

fun buildRetrofit(): Retrofit {
    return Retrofit.Builder()
        .client(buildClient())
        .baseUrl(BASE_URL)
        . addConverterFactory (GsonConverterFactory.create())
        .build()
}

fun buildApiService(): RemoteApiService =
    buildRetrofit().create(RemoteApiService::class.java)