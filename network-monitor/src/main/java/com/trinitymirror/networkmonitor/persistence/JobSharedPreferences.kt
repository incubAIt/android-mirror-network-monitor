package com.trinitymirror.networkmonitor.persistence

import android.content.Context


class JobSharedPreferences(context: Context) : JobPreferences {

    companion object {
        const val KEY_LAST_NOTIFICATION_TIMESTAMP_PREFIX = "listener_id=%s"
        const val KEY_LAST_BOOT_OFFSET = ""
        const val KEY_LAST_TRAFFIC_STATS = ""
        const val KEY_LAST_PERIOD_TICK = ""
        const val KEY_LAST_PERIOD_OFFSET = ""
    }

    private val sharedPref = context.getSharedPreferences(
            "network-monitor-prefs", Context.MODE_PRIVATE)


    override fun getLastNotificationTimestamp(listenerId: Int) =
        sharedPref.getLong(
                String.format(KEY_LAST_NOTIFICATION_TIMESTAMP_PREFIX, listenerId),
                0)

    override fun setLastNotificationTimestamp(listenerId: Int, timestamp: Long) {
        sharedPref.edit()
                .putLong(
                        String.format(KEY_LAST_NOTIFICATION_TIMESTAMP_PREFIX, listenerId),
                        timestamp)
                .apply()
    }

    override fun getLastBootOffset() =
        sharedPref.getLong(KEY_LAST_BOOT_OFFSET, 0)

    override fun setLastBootOffset(bytes: Long) {
        sharedPref.edit()
                .putLong(KEY_LAST_BOOT_OFFSET, bytes)
                .apply()
    }

    override fun getLastTrafficStats()
            = sharedPref.getLong(KEY_LAST_TRAFFIC_STATS, 0)

    override fun setLastTrafficStats(bytes: Long) {
        sharedPref.edit()
                .putLong(KEY_LAST_TRAFFIC_STATS, bytes)
                .apply()
    }

    override fun getLastPeriodTick() =
        sharedPref.getLong(KEY_LAST_PERIOD_TICK, 0)

    override fun setLastPeriodTick(timestamp: Long) {
        sharedPref.edit()
                .putLong(KEY_LAST_PERIOD_TICK, timestamp)
                .apply()
    }

    override fun getLastPeriodOffset() =
        sharedPref.getLong(KEY_LAST_PERIOD_OFFSET, 0)

    override fun setLastPeriodOffset(bytes: Long) {
        sharedPref.edit()
                .putLong(KEY_LAST_PERIOD_OFFSET, bytes)
                .apply()
    }


}