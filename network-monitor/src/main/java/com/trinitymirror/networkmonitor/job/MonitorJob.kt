package com.trinitymirror.networkmonitor.job

import com.trinitymirror.networkmonitor.NetworkMonitor
import com.trinitymirror.networkmonitor.NetworkMonitorServiceLocator
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.thresholdverifier.ThresholdVerifier
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
                .filter { hasNotTriggeredDuringLastPeriod(it) }
                .filter { isThresholdReached(it) }
                .forEach { handleThresholdReached(it) }
    }

    private fun hasNotTriggeredDuringLastPeriod(listener: UsageListener): Boolean {
        TODO()
    }

    private fun isThresholdReached(listener: UsageListener): Boolean {
        return thresholdVerifier.isThresholdReached(listener)
    }

    private fun handleThresholdReached(listener: UsageListener) {
        val result = thresholdVerifier.createResult(listener)

        listener.callback.onMaxBytesReached(result)

        //TODO Store in shared prefs ?
    }
}