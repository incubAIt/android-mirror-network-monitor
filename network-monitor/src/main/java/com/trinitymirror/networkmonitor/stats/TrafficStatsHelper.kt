package com.trinitymirror.networkmonitor.stats

import android.net.TrafficStats

object TrafficStatsHelper {

    val allRxBytes: Long
        get() = TrafficStats.getTotalRxBytes()

    val allTxBytes: Long
        get() = TrafficStats.getTotalTxBytes()

    val allRxBytesMobile: Long
        get() = TrafficStats.getMobileRxBytes()

    val allTxBytesMobile: Long
        get() = TrafficStats.getMobileTxBytes()

    val allRxBytesWifi: Long
        get() = TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()

    val allTxBytesWifi: Long
        get() = TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()

    fun getUidRxBytes(uid: Int): Long {
        return TrafficStats.getUidRxBytes(uid)
    }

    fun getUidTxBytes(uid: Int): Long {
        return TrafficStats.getUidTxBytes(uid)
    }

    fun getUidBytes(uid: Int): Long {
        return getUidRxBytes(uid) + getUidTxBytes(uid)
    }
}
