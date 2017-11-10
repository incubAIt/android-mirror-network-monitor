package com.trinitymirror.networkmonitor.monitorjob

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.support.annotation.RequiresApi
import com.trinitymirror.networkmonitor.UsageListener
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
     * Returns a [UsageListener.Result] object that aggregates information about the current traffic stats.
     */
    fun isThresholdReached(listener: UsageListener): UsageListener.Result?

    class BaseThresholdVerifier : ThresholdVerifier {

        override fun isThresholdReached(listener: UsageListener): UsageListener.Result? {
            val bytes = TrafficStatsHelper.uidBytes(Process.myUid())

            return if (isThresholdReached(bytes, listener)) {
                UsageListener.Result(0, UsageListener.Result.Extras(0, 0, 0, 0))
            } else {
                null
            }
        }

        private fun isThresholdReached(bytes: Long, listener: UsageListener) =
                bytes > listener.params.maxBytesSinceDeviceReboot
    }

    @RequiresApi(Build.VERSION_CODES.M)
    class MarshmallowThresholdVerifier(private val context: Context) : ThresholdVerifier {

        private val networkStatsHelper = NetworkStatsHelper(
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager)

        override fun isThresholdReached(listener: UsageListener): UsageListener.Result? {
            val bytes = queryPackageBytes(listener)

            return if (isThresholdReached(bytes, listener)) {
                UsageListener.Result(0, UsageListener.Result.Extras(0, 0, 0, 0))
            } else {
                null
            }
        }

        private fun isThresholdReached(bytes: Long, listener: UsageListener) =
                bytes > listener.params.maxBytesSinceLastPeriod

        private fun queryPackageBytes(listener: UsageListener): Long {
            return when (listener.params.networkType) {
                UsageListener.NetworkType.WIFI ->
                    networkStatsHelper.queryPackageBytesWifi(
                            Process.myUid(), listener.params.periodInMillis)

                UsageListener.NetworkType.MOBILE ->
                    networkStatsHelper.queryPackageBytesMobile(
                            Process.myUid(), Utils.getSubscriberId(context), listener.params.periodInMillis)
            }
        }
    }
}