package com.example.cryptohub

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        private lateinit var instance: App

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}