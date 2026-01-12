package com.astro.news.core.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitorInterface {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback: Flow<Boolean> =
        callbackFlow @RequiresPermission(
            Manifest.permission.ACCESS_NETWORK_STATE
        ) {
            val callback = object : ConnectivityManager.NetworkCallback() {
                private val networks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    networks.add(network)
                    trySend(networks.isNotEmpty())
                }

                override fun onLost(network: Network) {
                    networks.remove(network)
                    trySend(networks.isNotEmpty())
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(request, callback)

            trySend(isCurrentlyConnected())

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }

    override val isOnline: Flow<Boolean> = networkCallback.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = isCurrentlyConnected()
    )

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isCurrentlyConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

interface NetworkMonitorInterface{
    val isOnline: Flow<Boolean>
}
