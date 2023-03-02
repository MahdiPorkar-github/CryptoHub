package com.example.cryptohub.networking

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Builds retrofit client providing a base url and other configuration you might need.
 */

private const val HEADER_AUTHORIZATION = "Authorization"
fun buildClient(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(buildAuthorizationInterceptor())
        .build()

fun buildAuthorizationInterceptor() = Interceptor { chain ->
    val originalRequest = chain.request()
    val newRequest =
        originalRequest.newBuilder().addHeader(HEADER_AUTHORIZATION, API_KEY).build()
    chain.proceed(newRequest)
}

fun buildRetrofit(): Retrofit {
    return Retrofit.Builder()
        .client(buildClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun buildApiService(): RemoteApiService =
    buildRetrofit().create(RemoteApiService::class.java)