package com.saniya.bookhub.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {

    fun checkConnectivity(context: Context) : Boolean {

        // Get the networks of device: wifi, bluetooth etc.
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check Active networks
        val activeNetwork : NetworkInfo? = connectivityManager.activeNetworkInfo

        // Returns true if internet connected
        if (activeNetwork?.isConnected != null)
            return activeNetwork.isConnected
        else
            return false
    }
}