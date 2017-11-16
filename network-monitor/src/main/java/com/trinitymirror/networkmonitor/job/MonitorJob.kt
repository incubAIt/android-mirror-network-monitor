package com.trinitymirror.networkmonitor.job

import android.util.Log
import com.trinitymirror.networkmonitor.NetworkMonitor
import com.trinitymirror.networkmonitor.NetworkMonitorServiceLocator
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.persistence.JobPreferences
import com.trinitymirror.networkmonitor.thresholdverifier.ThresholdVerifier
import io.reactivex.Completable

/**
 * Monitoring job that gets executed every 2h to verify data usage.
 */
class MonitorJob(
        private val thresholdVerifier: ThresholdVerifier,
        private val jobPreferences: JobPreferences) {

    // empty constructor with default arguments
    constructor() : this(
            NetworkMonitorServiceLocator.provideThresholdVerifier(),
            NetworkMonitorServiceLocator.provideJobPreferences())

    fun execute(): Completable {
        return Completable.fromAction { executeAsync() }
    }

    private fun executeAsync() {
        Log.d(TAG, "Running network monitor job: ${NetworkMonitor.with().networkListeners}")

        NetworkMonitor.with()
                .networkListeners
                .filter { hasNotTriggeredDuringLastPeriod(it) }
                .filter { isThresholdReached(it) }
                .forEach { handleThresholdReached(it) }
    }

    private fun hasNotTriggeredDuringLastPeriod(listener: UsageListener): Boolean {
        val lastNotificationTimestamp = jobPreferences.getLastNotificationTimestamp(listener.id)
        val lastPeriod = System.currentTimeMillis() - listener.params.periodInMillis

        return lastPeriod > lastNotificationTimestamp
    }

    private fun isThresholdReached(listener: UsageListener): Boolean {
        return thresholdVerifier.isThresholdReached(listener)
    }

    private fun handleThresholdReached(listener: UsageListener) {
        Log.d(TAG, "Threshold reached on $listener")
        val result = thresholdVerifier.createResult(listener.params)

        listener.callback.onMaxBytesReached(result)

        jobPreferences.setLastNotificationTimestamp(
                listener.id, System.currentTimeMillis())
    }

    companion object {
        const val TAG = "MonitorJob"
    }
}