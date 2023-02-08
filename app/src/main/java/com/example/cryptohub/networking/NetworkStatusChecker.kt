package com.example.cryptohub.networking

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

// connectivityManager to check the current network information and its capabilities.
class NetworkStatusChecker(private val connectivityManager: ConnectivityManager?) {

    inline fun performIfConnectedToInternet(action: () -> Unit) {

        if (hasInternetConnection()) {
            action()
        }
    }

    fun hasInternetConnection(): Boolean {

        val network = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

    }

}