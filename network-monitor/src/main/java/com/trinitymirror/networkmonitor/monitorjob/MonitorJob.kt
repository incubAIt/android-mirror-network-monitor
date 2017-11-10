package com.trinitymirror.networkmonitor.monitorjob

import com.trinitymirror.networkmonitor.NetworkMonitor
import com.trinitymirror.networkmonitor.NetworkMonitorServiceLocator
import io.reactivex.Completable

/**
 * Monitoring job that gets executed every 2h to verify data usage.
 */
class MonitorJob(private val thresholdVerifier: ThresholdVerifier) {

    // empty constructor with default arguments
    constructor() : this(NetworkMonitorServiceLocator.provideThresholdVerifier())

    fun execute(): Completable {
        return Completable.fromAction { executeAsync() }
    }

    private fun executeAsync() {
        NetworkMonitor.with()
                .networkListeners
                .forEach { verifyThreshold(it) }
    }

    private fun verifyThreshold(listener: NetworkMonitor.UsageListener) {
        val result = thresholdVerifier.isThresholdReached(listener)
        if (result.isThresholdReached) {
            onThresholdReached(listener, result)
        }
    }

    private fun onThresholdReached(listener: NetworkMonitor.UsageListener, result: ThresholdVerifier.Result) {
        listener.callback.onMaxBytesReached(result)

        //TODO Store in shared prefs ?
    }

}