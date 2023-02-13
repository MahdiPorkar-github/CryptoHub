package com.example.cryptohub

import android.app.Application
import com.example.cryptohub.networking.RemoteApi
import com.example.cryptohub.networking.buildApiService

class App : Application() {

    companion object {
        private lateinit var instance: App

        private val apiService by lazy { buildApiService() }

        val remoteApi by lazy { RemoteApi(apiService) }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}