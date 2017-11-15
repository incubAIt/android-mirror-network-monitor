package com.trinitymirror.networkmonitor

import com.trinitymirror.networkmonitor.job.MonitorJobFactory
import com.trinitymirror.networkmonitor.usagecallback.UsageCallbackRegister

/**
 * NetworkMonitor is the main entry-point for the library, allowing users to register/unregister
 * listeners that notify whenever wifi/mobile traffic data exceeds a specified threshold.
 */
class NetworkMonitor private constructor(
        private val usageCallbacks: UsageCallbackRegister,
        private val monitorJobFactory: MonitorJobFactory) {

    internal val networkListeners = mutableListOf<UsageListener>()

    fun registerListener(listener: UsageListener) {
        usageCallbacks.registerUsageCallback(listener)
        networkListeners.add(listener)

        if (networkListeners.size == 1) {
            scheduleJob()
        }
    }

    fun unregisterListener(listener: UsageListener) {
        usageCallbacks.unregisterUsageCallback(listener)
        networkListeners.remove(listener)

        if (networkListeners.isEmpty()) {
            cancelJob()
        }
    }

    fun scheduleJob() {
        monitorJobFactory.scheduleJob()
    }

    fun cancelJob() {
        monitorJobFactory.cancelJob()
    }

    fun obtainCurrentStats(params: UsageListener.Params) : UsageListener.Result {
        return NetworkMonitorServiceLocator.provideThresholdVerifier()
                .createResult(params)
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