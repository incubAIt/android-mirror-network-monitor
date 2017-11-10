package com.trinitymirror.networkmonitor

import com.trinitymirror.networkmonitor.monitorjob.MonitorJobFactory
import com.trinitymirror.networkmonitor.monitorjob.ThresholdVerifier

/**
 * NetworkMonitor is the main entry-point for the library, allowing users to register/unregister
 * listeners that notify whenever wifi/mobile traffic data exceeds a specified threshold.
 */
class NetworkMonitor private constructor(
        private val usageCallbacks: UsageCallbackRegister,
        monitorJobFactory: MonitorJobFactory) {

    internal val networkListeners = mutableListOf<UsageListener>()

    init {
        monitorJobFactory.scheduleJob()
    }

    fun registerListener(listener: UsageListener) {
        usageCallbacks.registerUsageCallback(listener)
        networkListeners.add(listener)
    }

    fun unregisterListener(listener: UsageListener) {
        usageCallbacks.unregisterUsageCallback(listener)
        networkListeners.remove(listener)
    }

    /**
     * Object that uniquely identifies an UsageListener
     *
     * @param id [Int] provide an unique id for each registered listener
     * @param params [Params] provides information on when to trigger the callback
     * @param callback [Callback] to notify when the thresholds given by `params` are reached.
     */
    data class UsageListener(
            val id: Int, val params: Params, val callback: Callback) {

        enum class NetworkType { WIFI, MOBILE }

        /**
         * Provides information on when to trigger its corresponding [UsageListener]
         *
         * @param maxBytesSinceDeviceReboot bytes allowed since the device last rebooted.
         *  Used by [android.net.TrafficStats] `(API < 23)`.
         *
         * @param maxBytesSinceAppRestart bytes allowed since the app restart.
         *  Used by [android.app.usage.NetworkStatsManager.registerUsageCallback] `(API >= 24)`
         *
         * @param maxBytesSinceLastPeriod bytes allowed since the last [periodInMillis].
         *  Used by [android.app.usage.NetworkStatsManager] `(API >= 23)`
         *
         * @param periodInMillis on `API >= 23` this value specifies for how long
         * [maxBytesSinceLastPeriod] is accounted for.
         *
         * @param networkType Whether the thresholds applies for mobile or wifi.
         */
        data class Params(
                val maxBytesSinceDeviceReboot: Long,
                val maxBytesSinceAppRestart: Long,
                val maxBytesSinceLastPeriod: Long,
                val periodInMillis: Long,
                val networkType: NetworkType)

        /**
         * Callback that gets triggered when a threshold is reached
         */
        interface Callback {
            fun onMaxBytesReached(result: ThresholdVerifier.Result)
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
                    NetworkMonitorServiceLocator.provideUsageCallbackRegister(),
                    NetworkMonitorServiceLocator.provideMonitorJobFactory())
        }

        fun reset() {
            INSTANCE = null
        }
    }
}