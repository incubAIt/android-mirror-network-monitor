package com.trinitymirror.networkmonitor.persistence

import android.content.Context

class JobSharedPreferences(val context: Context) : JobPreferences {

    override fun getLastNotificationTimestamp(listenerId: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastNotificationTimestamp(listenerId: Int, timestamp: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastBootOffset(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastBootOffset(bytes: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastTrafficStats(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastTrafficStats(bytes: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastPeriodTick(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastPeriodTick(timestamp: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastPeriodOffset(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastPeriodOffset(bytes: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}