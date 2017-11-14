package com.trinitymirror.networkmonitor.persistence

/**
 * Created by ricardobelchior on 13/11/2017.
 */
interface JobPreferences {

    fun getLastNotificationTimestamp(listenerId: Int): Long

    fun setLastNotificationTimestamp(listenerId: Int, timestamp: Long)

    fun getLastBootOffset(): Long

    fun setLastBootOffset(bytes: Long)

    fun getLastTrafficStats(): Long

    fun setLastTrafficStats(bytes: Long)

    fun getLastPeriodTick(): Long

    fun setLastPeriodTick(timestamp: Long)

    fun getLastPeriodOffset(): Long

    fun setLastPeriodOffset(bytes: Long)
}