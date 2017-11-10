package com.trinitymirror.networkmonitor.monitorjob

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.support.annotation.RequiresApi
import com.trinitymirror.networkmonitor.NetworkMonitor
import com.trinitymirror.networkmonitor.stats.NetworkStatsHelper
import com.trinitymirror.networkmonitor.stats.TrafficStatsHelper
import com.trinitymirror.networkmonitor.stats.Utils

/**
 * Single-method interface to verify if traffic data was exceeded.
 * This interface has 2 implementations: [BaseThresholdVerifier] for `API < 23` and
 * [MarshmallowThresholdVerifier] for `API >= 23`
 *
 * @see [isThresholdReached]
 */
interface ThresholdVerifier {

    /**
     * Verifies if the threshold is exceeded for the given listener.
     * Returns a [Result] object that aggregates information about the current traffic stats.
     */
    fun isThresholdReached(listener: NetworkMonitor.UsageListener): Result

    data class Result(val isThresholdReached: Boolean, val reason: Int)

    class BaseThresholdVerifier : ThresholdVerifier {

        override fun isThresholdReached(listener: NetworkMonitor.UsageListener): Result {
            val bytes = TrafficStatsHelper.getUidBytes(Process.myUid())
            return Result(bytes > listener.params.maxBytesSinceDeviceReboot, 0)//TODO("add proper result")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    class MarshmallowThresholdVerifier(private val context: Context) : ThresholdVerifier {

        private val networkStatsHelper = NetworkStatsHelper(
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager)

        override fun isThresholdReached(listener: NetworkMonitor.UsageListener): Result {
            return when (listener.params.networkType) {
                NetworkMonitor.UsageListener.NetworkType.WIFI -> verifyOnWifi(listener)
                NetworkMonitor.UsageListener.NetworkType.MOBILE -> verifyOnMobile(listener)
            }
        }

        private fun verifyOnWifi(listener: NetworkMonitor.UsageListener): Result {
            val bytes = networkStatsHelper.queryPackageBytesWifi(Process.myUid(), listener.params.periodInMillis)
            return Result(bytes > listener.params.maxBytesSinceLastPeriod, 0)//TODO("add proper result")
        }

        private fun verifyOnMobile(listener: NetworkMonitor.UsageListener): Result {
            val bytes = networkStatsHelper.queryPackageBytesMobile(
                    Process.myUid(), Utils.getSubscriberId(context), listener.params.periodInMillis)

            return Result(bytes > listener.params.maxBytesSinceLastPeriod, 0)//TODO("add proper result")
        }
    }
}