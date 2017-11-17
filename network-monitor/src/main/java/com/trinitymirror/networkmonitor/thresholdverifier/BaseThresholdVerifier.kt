package com.trinitymirror.networkmonitor.thresholdverifier

import android.os.Process
import android.util.Log
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.persistence.JobPreferences
import com.trinitymirror.networkmonitor.stats.CurrentTimeInMillis
import com.trinitymirror.networkmonitor.stats.TrafficStatsHelper
import java.text.DateFormat
import java.util.*

/**
 * Implementation of [ThresholdVerifier] that works for API < 23.
 * Uses the old and not-so-reliable [android.net.TrafficStats].
 */
internal class BaseThresholdVerifier(
        private val trafficStatsHelper: TrafficStatsHelper,
        private val jobPreferences: JobPreferences,
        private val currentTimeInMillis: CurrentTimeInMillis) : ThresholdVerifier {

    var totalBytesSinceLastPeriod: Long = -1

    override fun isThresholdReached(listener: UsageListener): Boolean {
        val bytesSinceLastPeriod = getTotalBytesSinceLastPeriod(listener.params.periodInMillis)
        val maxBytesSinceLastPeriod = listener.params.maxBytesSinceLastPeriod

        return bytesSinceLastPeriod > maxBytesSinceLastPeriod
    }

    override fun createResult(params: UsageListener.Params): UsageListener.Result {
        val uid = Process.myUid()

        return UsageListener.Result(
                UsageListener.ResultCode.MAX_BYTES_SINCE_LAST_PERIOD_COMPAT,
                UsageListener.Result.Extras(
                        -1, -1, -1, -1,
                        trafficStatsHelper.uidRxBytes(uid),
                        trafficStatsHelper.uidTxBytes(uid),
                        getTotalBytesSinceLastPeriod(params.periodInMillis)))
    }

    private fun getTotalBytesSinceLastPeriod(periodInMillis: Long): Long {
        if (totalBytesSinceLastPeriod < 0) {
            totalBytesSinceLastPeriod = calculateTotalBytesSinceLastPeriod(periodInMillis)
        }
        return totalBytesSinceLastPeriod
    }

    private fun calculateTotalBytesSinceLastPeriod(periodInMillis: Long): Long {

        val uid = Process.myUid()
        val currentTime = currentTimeInMillis.obtain()
        val currentTrafficStats = trafficStatsHelper.uidBytes(uid)

        val lastBootOffset = jobPreferences.getLastBootOffset()
        val lastTrafficStats = jobPreferences.getLastTrafficStats()
        val bootOffset =
                if (currentTrafficStats < lastTrafficStats) lastTrafficStats + lastBootOffset
                else lastBootOffset
        val lastKnownAppTraffic = currentTrafficStats + bootOffset
        val lastPeriodTick = jobPreferences.getLastPeriodTick()
        val periodTick =
                if ((currentTime - periodInMillis) >= lastPeriodTick) currentTime
                else lastPeriodTick
        val lastPeriodOffset = jobPreferences.getLastPeriodOffset()
        val periodOffset =
                if (periodTick == currentTime) lastKnownAppTraffic
                else lastPeriodOffset
        val totalBytesSinceLastPeriod = lastKnownAppTraffic - periodOffset

        jobPreferences.setLastBootOffset(bootOffset)
        jobPreferences.setLastTrafficStats(currentTrafficStats)
        jobPreferences.setLastPeriodTick(periodTick)
        jobPreferences.setLastPeriodOffset(periodOffset)

        //print(currentTime, lastPeriodTick, periodTick, lastTrafficStats, currentTrafficStats, lastBootOffset, bootOffset, lastKnownAppTraffic, lastPeriodOffset, periodOffset, totalBytesSinceLastPeriod)
        return totalBytesSinceLastPeriod
    }

    private fun print(currentTime: Long, lastPeriodTick: Long, periodTick: Long, lastTrafficStats: Long, currentTrafficStats: Long, lastBootOffset: Long, bootOffset: Long, lastKnownAppTraffic: Long, lastPeriodOffset: Long, periodOffset: Long, totalBytesSinceLastPeriod: Long) {
        print("currentTime ${formatTimestamp(currentTime)}")
        print("lastPeriodTick ${formatTimestamp(lastPeriodTick)}")
        print("periodTick ${formatTimestamp(periodTick)}")
        print("lastTrafficStats ${formatBytes(lastTrafficStats)}")
        print("currentTrafficStats ${formatBytes(currentTrafficStats)}")
        print("lastBootOffset ${formatBytes(lastBootOffset)}")
        print("bootOffset ${formatBytes(bootOffset)}")
        print("lastKnownAppTraffic ${formatBytes(lastKnownAppTraffic)}")
        print("lastPeriodOffset ${formatBytes(lastPeriodOffset)}")
        print("periodOffset ${formatBytes(periodOffset)}")
        print("totalBytesSinceLastPeriod ${formatBytes(totalBytesSinceLastPeriod)}")
        print("------------------------")
    }

    private fun print(text: String) {
        Log.d("TAG",text)
        System.out.println(text)
    }

    private fun formatBytes(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return bytes.toString() + " B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "i"
        return String.format(Locale.UK, "%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    private fun formatTimestamp(timestamp: Long): String {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.UK)
                .format(Date(timestamp))
    }

}