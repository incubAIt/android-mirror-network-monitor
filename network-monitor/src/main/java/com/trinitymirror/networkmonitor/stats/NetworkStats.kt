package com.trinitymirror.networkmonitor.stats

import com.trinitymirror.networkmonitor.NetworkMonitor

interface NetworkStats {

    fun getMobileRx(): Long
    fun getMobileTx(): Long
    fun getWifiRx(): Long
    fun getWifiTx(): Long
    fun getTotalRx(): Long
    fun getTotalTx(): Long

    fun registerUsageCallback(listener: NetworkMonitor.UsageListener)

    fun unregisterUsageCallback(listener: NetworkMonitor.UsageListener)
}