package com.trinitymirror.networkmonitor.stats

import android.net.TrafficStats

object TrafficStatsHelper {

    fun allRxBytes() = TrafficStats.getTotalRxBytes()

    fun allTxBytes() = TrafficStats.getTotalTxBytes()

    fun allRxBytesMobile() = TrafficStats.getMobileRxBytes()

    fun allTxBytesMobile() = TrafficStats.getMobileTxBytes()

    fun allRxBytesWifi() = TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()

    fun allTxBytesWifi() = TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()

    fun uidRxBytes(uid: Int) = TrafficStats.getUidRxBytes(uid)

    fun uidTxBytes(uid: Int) = TrafficStats.getUidTxBytes(uid)

    fun uidBytes(uid: Int) = uidRxBytes(uid) + uidTxBytes(uid)
}
