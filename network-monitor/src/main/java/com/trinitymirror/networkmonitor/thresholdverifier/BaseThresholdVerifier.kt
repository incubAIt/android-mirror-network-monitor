package com.trinitymirror.networkmonitor.thresholdverifier

import android.os.Process
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.persistence.JobPreferences
import com.trinitymirror.networkmonitor.stats.TrafficStatsHelper

/**
 * Implementation of [ThresholdVerifier] that works for API < 23.
 * Uses the old and not-so-reliable [android.net.TrafficStats].
 */
class BaseThresholdVerifier(val jobPreferences: JobPreferences) : ThresholdVerifier {

    override fun isThresholdReached(listener: UsageListener): Boolean {
        val uid = Process.myUid()
        val currentTime = System.currentTimeMillis()
        val currentBytes = TrafficStatsHelper.uidBytes(uid)

        val lastBootOffset = jobPreferences.getLastBootOffset()
        val lastTrafficStats = jobPreferences.getLastTrafficStats()
        val bootOffset =
                if (currentBytes < lastTrafficStats) lastTrafficStats
                else lastBootOffset
        val lastKnownAppTraffic = currentBytes + bootOffset
        val lastPeriodTick = jobPreferences.getLastPeriodTick()
        val periodTick =
                if ((currentTime - listener.params.periodInMillis) > lastPeriodTick) currentTime
                else lastPeriodTick
        val lastPeriodOffset = jobPreferences.getLastPeriodOffset()
        val periodOffset =
                if (periodTick == currentTime) lastKnownAppTraffic
                else lastPeriodOffset
        val totalBytesSinceLastPeriod = lastKnownAppTraffic - periodOffset

        jobPreferences.setLastBootOffset(bootOffset)
        jobPreferences.setLastTrafficStats(currentBytes)
        jobPreferences.setLastPeriodTick(periodTick)
        jobPreferences.setLastPeriodOffset(periodOffset)

        return totalBytesSinceLastPeriod > listener.params.maxBytesSinceLastPeriod
    }

    override fun createResult(listener: UsageListener): UsageListener.Result {
        val uid = Process.myUid()
        return UsageListener.Result(
                UsageListener.ResultCode.MAX_BYTES_SINCE_LAST_PERIOD_COMPAT,
                UsageListener.Result.Extras(
                        -1, -1, -1, -1,
                        TrafficStatsHelper.uidRxBytes(uid),
                        TrafficStatsHelper.uidTxBytes(uid)))
    }
}