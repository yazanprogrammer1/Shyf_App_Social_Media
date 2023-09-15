package com.example.shyf_.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    private lateinit var connectivityManager: ConnectivityManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d("MyReceiver", "تم الاتصال بالانترنت")
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d("MyReceiver", "فقدت الاتصال بالانترنت")
                }
            }

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }
}
