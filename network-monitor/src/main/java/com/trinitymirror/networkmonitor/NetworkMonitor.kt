package com.trinitymirror.networkmonitor

import com.trinitymirror.networkmonitor.monitorjob.MonitorJobFactory

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