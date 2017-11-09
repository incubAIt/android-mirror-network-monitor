package com.trinitymirror.networkmonitor

import com.trinitymirror.networkmonitor.monitorjob.MonitorJobFactory
import com.trinitymirror.networkmonitor.monitorjob.ThresholdVerifier

/**
 * Created by ricardobelchior on 09/11/2017.
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