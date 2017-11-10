package com.trinitymirror.networkmonitor

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.util.SparseArrayCompat
import com.trinitymirror.networkmonitor.stats.Utils

/**
 * Interface with the Nougat-only feature of [android.app.usage.NetworkStatsManager.registerUsageCallback]
 * and [android.app.usage.NetworkStatsManager.unregisterUsageCallback]
 */
internal interface UsageCallbackRegister {

    fun registerUsageCallback(listener: UsageListener)
    fun unregisterUsageCallback(listener: UsageListener)

    @RequiresApi(Build.VERSION_CODES.N)
    open class Nougat(private val context: Context) : UsageCallbackRegister {
        protected val usageCallbacksList = SparseArrayCompat<NetworkStatsManager.UsageCallback>()

        override fun registerUsageCallback(listener: UsageListener) {
            val thresholdBytes = listener.params.maxBytesSinceAppRestart

            val networkType = if (listener.params.networkType == UsageListener.NetworkType.MOBILE)
                ConnectivityManager.TYPE_MOBILE else ConnectivityManager.TYPE_WIFI

            val subscriberId = if (listener.params.networkType == UsageListener.NetworkType.MOBILE)
                getSubscriberId() else ""

            val usageCallback = object : NetworkStatsManager.UsageCallback() {
                override fun onThresholdReached(networkType: Int, subscriberId: String?) {
                    onThresholdReached(listener)
                }
            }
            usageCallbacksList.put(listener.id, usageCallback)

            getNetworkStatsManager()
                    .registerUsageCallback(networkType, subscriberId, thresholdBytes, usageCallback)
        }

        open fun onThresholdReached(listener: UsageListener) {
            //val code = TODO("create code enums")
            //listener.callback.onMaxBytesReached(code)

            // store warning in shared prefs?
            // unregister callback
        }

        override fun unregisterUsageCallback(listener: UsageListener) {
            val usageCallback = usageCallbacksList.get(listener.id)
            getNetworkStatsManager()
                    .unregisterUsageCallback(usageCallback)

            usageCallbacksList.remove(listener.id)
        }

        open fun getSubscriberId() = Utils.getSubscriberId(context)

        open fun getNetworkStatsManager() =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    class Empty : UsageCallbackRegister {
        override fun registerUsageCallback(listener: UsageListener) {
        }

        override fun unregisterUsageCallback(listener: UsageListener) {
        }
    }
}