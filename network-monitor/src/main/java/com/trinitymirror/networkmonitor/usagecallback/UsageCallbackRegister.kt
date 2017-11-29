package com.trinitymirror.networkmonitor.usagecallback

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.support.annotation.RequiresApi
import android.support.v4.util.SparseArrayCompat
import android.util.Log
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.stats.NetworkStatsHelper
import com.trinitymirror.networkmonitor.stats.Utils

/**
 * Interface with the Nougat-only feature of [android.app.usage.NetworkStatsManager.registerUsageCallback]
 * and [android.app.usage.NetworkStatsManager.unregisterUsageCallback]
 */
internal interface UsageCallbackRegister {

    fun registerUsageCallback(listener: UsageListener)
    fun unregisterUsageCallback(listener: UsageListener)

    @RequiresApi(Build.VERSION_CODES.N)
    open class Nougat(private val context: Context, private val networkStatsManager: NetworkStatsManager,
                      private val mainHandler: Handler = Handler(Looper.getMainLooper())) : UsageCallbackRegister {

        private val uid = Process.myUid()
        private val networkStatsHelper = NetworkStatsHelper(networkStatsManager)
        protected val usageCallbacksList = SparseArrayCompat<NetworkStatsManager.UsageCallback>()

        override fun registerUsageCallback(listener: UsageListener) {
            val thresholdBytes = listener.params.maxBytesSinceAppRestart

            val networkType = mapNetworkType(listener)
            val subscriberId = mapSubscriberId(listener)
            val usageCallback = object : NetworkStatsManager.UsageCallback() {
                override fun onThresholdReached(networkType: Int, subscriberId: String?) {
                    onThresholdReached(listener)
                }
            }
            usageCallbacksList.put(listener.id, usageCallback)

            networkStatsManager
                    .registerUsageCallback(networkType, subscriberId, thresholdBytes, usageCallback, mainHandler)
        }

        private fun mapSubscriberId(listener: UsageListener) =
                if (listener.params.networkType == UsageListener.NetworkType.MOBILE)
                    getSubscriberId() else ""

        private fun mapNetworkType(listener: UsageListener) =
                if (listener.params.networkType == UsageListener.NetworkType.MOBILE)
                    ConnectivityManager.TYPE_MOBILE else ConnectivityManager.TYPE_WIFI

        open fun onThresholdReached(listener: UsageListener) {
            Log.d("UsageCallbackRegister", "Threshold reached on $listener")

            listener.callback.onMaxBytesReached(buildResult(listener))
            unregisterUsageCallback(listener)
        }

        private fun buildResult(listener: UsageListener): UsageListener.Result {
            val subscriberId = getSubscriberId()
            val rxMobile = rxMobile(listener, subscriberId)
            val txMobile = txMobile(listener, subscriberId)
            val rxWifi = rxWifi(listener)
            val txWifi = txWifi(listener)
            val rxBytes = rxMobile + rxWifi
            val txBytes = txMobile + txWifi

            return UsageListener.Result(
                    UsageListener.ResultCode.MAX_BYTES_SINCE_APP_RESTART,
                    UsageListener.Result.Extras(
                            rxMobile, txMobile,
                            rxWifi, txWifi,
                            rxBytes, txBytes))
        }

        override fun unregisterUsageCallback(listener: UsageListener) {
            usageCallbacksList.get(listener.id)?.let {
                networkStatsManager.unregisterUsageCallback(it)
            }

            usageCallbacksList.remove(listener.id)
        }

        private fun rxMobile(listener: UsageListener, subscriberId: String) =
                networkStatsHelper.queryPackageRxBytesMobile(
                        uid, subscriberId, listener.params.periodInMillis)

        private fun txMobile(listener: UsageListener, subscriberId: String) =
                networkStatsHelper.queryPackageTxBytesMobile(
                        uid, subscriberId, listener.params.periodInMillis)

        private fun rxWifi(listener: UsageListener) =
                networkStatsHelper.queryPackageRxBytesWifi(uid, listener.params.periodInMillis)

        private fun txWifi(listener: UsageListener) =
                networkStatsHelper.queryPackageTxBytesWifi(uid, listener.params.periodInMillis)

        open fun getSubscriberId() = Utils.getSubscriberId(context)
    }

    class Empty : UsageCallbackRegister {
        override fun registerUsageCallback(listener: UsageListener) {
        }

        override fun unregisterUsageCallback(listener: UsageListener) {
        }
    }
}