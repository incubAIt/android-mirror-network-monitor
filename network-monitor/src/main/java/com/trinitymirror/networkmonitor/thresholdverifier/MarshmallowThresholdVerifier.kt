package com.trinitymirror.networkmonitor.thresholdverifier

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.support.annotation.RequiresApi
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.stats.NetworkStatsHelper
import com.trinitymirror.networkmonitor.stats.Utils

@RequiresApi(Build.VERSION_CODES.M)
class MarshmallowThresholdVerifier(private val context: Context) : ThresholdVerifier {

    private val uid = Process.myUid()

    private val networkStatsHelper = NetworkStatsHelper(
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager)

    override fun isThresholdReached(listener: UsageListener): Boolean {
        val bytes = queryPackageBytes(listener)
        return isThresholdReached(bytes, listener)
    }

    private fun isThresholdReached(bytes: Long, listener: UsageListener) =
            bytes > listener.params.maxBytesSinceLastPeriod

    override fun createResult(params: UsageListener.Params): UsageListener.Result {
        val subscriberId = getSubscriberId()
        val rxMobile = rxMobile(params, subscriberId)
        val txMobile = txMobile(params, subscriberId)
        val rxWifi = rxWifi(params)
        val txWifi = txWifi(params)
        val rxBytes = rxMobile + rxWifi
        val txBytes = txMobile + txWifi

        return UsageListener.Result(
                UsageListener.ResultCode.MAX_BYTES_SINCE_LAST_PERIOD,
                UsageListener.Result.Extras(
                        rxMobile, txMobile,
                        rxWifi, txWifi,
                        rxBytes, txBytes))
    }

    private fun queryPackageBytes(listener: UsageListener): Long {
        return when (listener.params.networkType) {
            UsageListener.NetworkType.WIFI ->
                networkStatsHelper.queryPackageBytesWifi(
                        uid, listener.params.periodInMillis)

            UsageListener.NetworkType.MOBILE ->
                networkStatsHelper.queryPackageBytesMobile(
                        uid, getSubscriberId(), listener.params.periodInMillis)
        }
    }

    private fun rxMobile(params: UsageListener.Params, subscriberId: String) =
            networkStatsHelper.queryPackageRxBytesMobile(
                    uid, subscriberId, params.periodInMillis)

    private fun txMobile(params: UsageListener.Params, subscriberId: String) =
            networkStatsHelper.queryPackageTxBytesMobile(
                    uid, subscriberId, params.periodInMillis)

    private fun rxWifi(params: UsageListener.Params) =
            networkStatsHelper.queryPackageRxBytesWifi(uid, params.periodInMillis)

    private fun txWifi(params: UsageListener.Params) =
            networkStatsHelper.queryPackageTxBytesWifi(uid, params.periodInMillis)

    private fun getSubscriberId() = Utils.getSubscriberId(context)
}