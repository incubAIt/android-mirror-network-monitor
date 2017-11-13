package com.trinitymirror.networkmonitor.thresholdverifier

import android.os.Process
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.stats.TrafficStatsHelper

class BaseThresholdVerifier : ThresholdVerifier {

    override fun isThresholdReached(listener: UsageListener): Boolean {
        val uid = Process.myUid()
        val bytes = TrafficStatsHelper.uidBytes(uid)
        return isThresholdReached(bytes, listener)
    }

    private fun isThresholdReached(bytes: Long, listener: UsageListener) =
            bytes > listener.params.maxBytesSinceDeviceBoot

    override fun createResult(listener: UsageListener): UsageListener.Result {
        val uid = Process.myUid()
        return UsageListener.Result(
                UsageListener.ResultCode.MAX_BYTES_SINCE_DEVICE_BOOT,
                UsageListener.Result.Extras(
                        -1, -1, -1, -1,
                        TrafficStatsHelper.uidRxBytes(uid),
                        TrafficStatsHelper.uidTxBytes(uid)))
    }
}