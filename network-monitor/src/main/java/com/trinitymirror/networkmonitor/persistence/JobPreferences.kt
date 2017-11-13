package com.trinitymirror.networkmonitor.persistence

/**
 * Created by ricardobelchior on 13/11/2017.
 */
interface JobPreferences {

    fun getLastNotificationTimestamp(listenerId: Int): Long

    fun setLastNotificationTimestamp(listenerId: Int, timestamp: Long)

    fun getLastKnownAppTraffic(): Long

    fun setLastKnownAppTraffic(bytes: Long)
}