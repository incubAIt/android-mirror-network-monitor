package com.trinitymirror.networkmonitor

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.util.SparseArrayCompat
import com.trinitymirror.networkmonitor.stats.Utils

interface UsageCallbackRegister {

    /**
     *
     */
    fun registerUsageCallback(listener: NetworkMonitor.UsageListener)

    /**
     *
     */
    fun unregisterUsageCallback(listener: NetworkMonitor.UsageListener)

    @RequiresApi(Build.VERSION_CODES.N)
    class Nougat(private val context: Context) : UsageCallbackRegister {
        private val usageCallbacksList = SparseArrayCompat<NetworkStatsManager.UsageCallback>()

        override fun registerUsageCallback(listener: NetworkMonitor.UsageListener) {
            val thresholdBytes = listener.params.maxBytesSinceAppRestart

            val networkType = if (listener.params.networkType == NetworkMonitor.UsageListener.NetworkType.MOBILE)
                ConnectivityManager.TYPE_MOBILE else ConnectivityManager.TYPE_WIFI

            val subscriberId = if (listener.params.networkType == NetworkMonitor.UsageListener.NetworkType.MOBILE)
                Utils.getSubscriberId(context) else ""

            val usageCallback = object : NetworkStatsManager.UsageCallback() {
                override fun onThresholdReached(networkType: Int, subscriberId: String?) {
                    val reason = TODO("create reason enums")
                    listener.callback.onMaxBytesReached(reason)

                    // store warning in shared prefs?
                }
            }
            usageCallbacksList.put(listener.id, usageCallback)

            getNetworkStatsManager()
                    .registerUsageCallback(networkType, subscriberId, thresholdBytes, usageCallback)
        }

        override fun unregisterUsageCallback(listener: NetworkMonitor.UsageListener) {
            val usageCallback = usageCallbacksList.get(listener.id)
            getNetworkStatsManager()
                    .unregisterUsageCallback(usageCallback)
        }

        private fun getNetworkStatsManager() =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    class Empty : UsageCallbackRegister {
        override fun registerUsageCallback(listener: NetworkMonitor.UsageListener) {
        }

        override fun unregisterUsageCallback(listener: NetworkMonitor.UsageListener) {
        }
    }
}