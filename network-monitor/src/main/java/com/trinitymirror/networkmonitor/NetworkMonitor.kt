package com.trinitymirror.networkmonitor

import android.app.usage.NetworkStatsManager
import android.content.Context
import com.trinitymirror.networkmonitor.monitorjob.MonitorJobFactory
import com.trinitymirror.networkmonitor.stats.NetworkStats

/**
 * Created by ricardobelchior on 09/11/2017.
 */
class NetworkMonitor private constructor(
        private val context: Context,
        private val networkStats: NetworkStats,
        monitorJobFactory: MonitorJobFactory) {

    internal val networkListeners = mutableListOf<UsageListener>()
    internal val usageCallbacksList = mutableListOf<NetworkStatsManager.UsageCallback>()

    init {
        monitorJobFactory.scheduleJob()
    }

    fun registerListener(listener: UsageListener) {
        networkStats.registerUsageCallback(listener)
        networkListeners.add(listener)
    }

//    private fun registerUsageCallback(listener: UsageListener) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            return
//        }
//
//        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager)
//        val thresholdBytes = listener.params.maxBytesSinceAppRestart
//
//        val networkType = if (listener.params.networkType == UsageListener.NetworkType.MOBILE)
//            ConnectivityManager.TYPE_MOBILE else ConnectivityManager.TYPE_WIFI
//
//        val subscriberId = if (listener.params.networkType == UsageListener.NetworkType.MOBILE)
//            Utils.getSubscriberId(context) else ""
//
//        val usageCallback = object : NetworkStatsManager.UsageCallback() {
//            override fun onThresholdReached(networkType: Int, subscriberId: String?) {
//                listener.callback.onMaxBytesReached()
//            }
//        }
//
//        networkStatsManager.registerUsageCallback(networkType, subscriberId, thresholdBytes, )
//    }

    fun unregisterListener(listener: UsageListener) {
        networkStats.unregisterUsageCallback(listener)
        networkListeners.remove(listener)
    }

    //TODO isThresholdReached if id is necessary
    data class UsageListener(
            val id: Int, val params: Params, val callback: Callback) {

        enum class NetworkType { WIFI, MOBILE }

        data class Params(
                val maxBytesSinceDeviceReboot: Long,
                val maxBytesSinceAppRestart: Long,
                val maxBytesSinceLastPeriod: Long,
                val periodInMillis: Long,
                val networkType: NetworkType)

        interface Callback {
            fun onMaxBytesReached(reason: Int)
        }
    }

    companion object {
        @Volatile private var INSTANCE: NetworkMonitor? = null

        @JvmStatic
        fun with(): NetworkMonitor =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: create().also { NetworkMonitor.INSTANCE = it }
                }

        private fun create(): NetworkMonitor {
            return NetworkMonitor(
                    NetworkMonitorServiceLocator.context,
                    NetworkMonitorServiceLocator.provideNetworkStats(),
                    NetworkMonitorServiceLocator.provideMonitorJobFactory())
        }

        fun reset() {
            INSTANCE = null
        }
    }
}