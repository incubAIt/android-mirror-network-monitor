package com.trinitymirror.networkmonitor.persistence

class InMemoryPreferences : JobPreferences {

    var lastNotificationTimestamps = mutableMapOf<Int, Long>()
    var inMemoryLastBootOffset = 0L
    var inMemoryLastTrafficStats = 0L
    var inMemoryLastPeriodTick = 0L
    var inMemoryLastPeriodOffset = 0L

    override fun getLastNotificationTimestamp(listenerId: Int): Long {
        return lastNotificationTimestamps.getOrDefault(listenerId, 0)
    }

    override fun setLastNotificationTimestamp(listenerId: Int, timestamp: Long) {
        lastNotificationTimestamps.put(listenerId, timestamp)
    }

    override fun getLastBootOffset(): Long {
        return inMemoryLastBootOffset
    }

    override fun setLastBootOffset(bytes: Long) {
        inMemoryLastBootOffset = bytes
    }

    override fun getLastTrafficStats(): Long {
        return inMemoryLastTrafficStats
    }

    override fun setLastTrafficStats(bytes: Long) {
        inMemoryLastTrafficStats = bytes
    }

    override fun getLastPeriodTick(): Long {
        return inMemoryLastPeriodTick
    }

    override fun setLastPeriodTick(timestamp: Long) {
        inMemoryLastPeriodTick = timestamp
    }

    override fun getLastPeriodOffset(): Long {
        return inMemoryLastPeriodOffset
    }

    override fun setLastPeriodOffset(bytes: Long) {
        inMemoryLastPeriodOffset = bytes
    }
}