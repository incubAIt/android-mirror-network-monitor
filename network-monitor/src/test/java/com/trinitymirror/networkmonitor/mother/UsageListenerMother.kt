package com.trinitymirror.networkmonitor.mother

import com.trinitymirror.networkmonitor.UsageListener

object UsageListenerMother {

    fun create(id: Int,
               networkType: UsageListener.NetworkType,
               callback: UsageListener.Callback): UsageListener {

        return UsageListener(
                id, params(networkType), callback)
    }

    fun create(id: Int): UsageListener {
        return UsageListener(
                id, params(), callback())
    }

    fun create(): UsageListener {
        return UsageListener(
                1,
                params(),
                callback())
    }

    private fun callback(): UsageListener.Callback {
        return object : UsageListener.Callback {
            override fun onMaxBytesReached(result: UsageListener.Result) {
            }
        }
    }

    private fun params() = params(UsageListener.NetworkType.MOBILE)

    private fun params(networkType: UsageListener.NetworkType) = UsageListener.Params(
            100,
            200,
            300,
            400,
            networkType)
}