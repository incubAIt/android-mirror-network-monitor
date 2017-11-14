package com.trinitymirror.networkmonitor.mother

import com.trinitymirror.networkmonitor.UsageListener

object UsageListenerMother {

    fun create(id: Int = 1,
               networkType: UsageListener.NetworkType,
               callback: UsageListener.Callback): UsageListener {

        return UsageListener(
                id, params(networkType = networkType), callback)
    }

    fun create(id: Int = 1,
               params: UsageListener.Params = params(),
               callback: UsageListener.Callback = callback()): UsageListener {

        return UsageListener(
                id,
                params,
                callback)
    }

    private fun callback(): UsageListener.Callback {
        return object : UsageListener.Callback {
            override fun onMaxBytesReached(result: UsageListener.Result) {
            }
        }
    }

    fun params(
            maxBytesSinceAppRestart: Long = 200,
            maxBytesSinceLastPeriod: Long = 300,
            periodInMillis: Long = 400,
            networkType: UsageListener.NetworkType = UsageListener.NetworkType.MOBILE) = UsageListener.Params(
            maxBytesSinceAppRestart,
                    maxBytesSinceLastPeriod,
                    periodInMillis,
                    networkType)

    fun result(code: UsageListener.ResultCode = UsageListener.ResultCode.MAX_BYTES_SINCE_LAST_PERIOD) =
            UsageListener.Result(code,
                    UsageListener.Result.Extras(0, 0, 0, 0, 0, 0))

}