package com.trinitymirror.networkmonitor.stats

internal interface CurrentTimeInMillis {

    fun obtain(): Long

    class SystemTime : CurrentTimeInMillis {
        override fun obtain(): Long {
            return System.currentTimeMillis()
        }
    }
}