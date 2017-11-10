package com.trinitymirror.networkmonitor.mother

import com.trinitymirror.networkmonitor.NetworkMonitor
import com.trinitymirror.networkmonitor.monitorjob.ThresholdVerifier

object UsageListenerMother {

    fun create(id: Int,
               networkType: NetworkMonitor.UsageListener.NetworkType,
               callback: NetworkMonitor.UsageListener.Callback): NetworkMonitor.UsageListener {

        return NetworkMonitor.UsageListener(
                id, params(networkType), callback)
    }

    fun create(id: Int): NetworkMonitor.UsageListener {
        return NetworkMonitor.UsageListener(
                id, params(), callback())
    }

    fun create(): NetworkMonitor.UsageListener {
        return NetworkMonitor.UsageListener(
                1,
                params(),
                callback())
    }

    private fun callback(): NetworkMonitor.UsageListener.Callback {
        return object : NetworkMonitor.UsageListener.Callback {
            override fun onMaxBytesReached(result: ThresholdVerifier.Result) {
            }
        }
    }

    private fun params() = params(NetworkMonitor.UsageListener.NetworkType.MOBILE)

    private fun params(networkType: NetworkMonitor.UsageListener.NetworkType) = NetworkMonitor.UsageListener.Params(
            100,
            200,
            300,
            400,
            networkType)
}