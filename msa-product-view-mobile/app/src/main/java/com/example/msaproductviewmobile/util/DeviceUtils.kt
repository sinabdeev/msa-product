package com.example.msaproductviewmobile.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

/**
 * Utility object for common device and network operations.
 */
object DeviceUtils {

    /**
     * Check if the device has an active internet connection.
     */
    @SuppressLint("MissingPermission")
    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
            ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
