package com.trinitymirror.networkmonitor

/**
 * Object that uniquely identifies an UsageListener
 *
 * @param id [Int] provide an unique id for each registered listener
 * @param params [Params] provides information on when to trigger the callback
 * @param callback [Callback] to notify when the thresholds given by `params` are reached.
 */
data class UsageListener(
        val id: Int, val params: Params, val callback: Callback) {

    enum class NetworkType { WIFI, MOBILE }

    enum class ResultCode {
        MAX_BYTES_SINCE_LAST_PERIOD,
        MAX_BYTES_SINCE_LAST_PERIOD_COMPAT,
        MAX_BYTES_SINCE_APP_RESTART
    }

    /**
     * Provides information on when to trigger its corresponding [UsageListener]
     *
     * @param maxBytesSinceAppRestart bytes allowed since the app restart.
     *  Used by [android.app.usage.NetworkStatsManager.registerUsageCallback] `(API >= 24)`
     *
     * @param maxBytesSinceLastPeriod bytes allowed since the last [periodInMillis].
     *  Used by [android.app.usage.NetworkStatsManager] `(API >= 23)` or [android.net.TrafficStats] `(API < 23)`
     *
     * @param periodInMillis on `API >= 23` this value specifies for how long
     * [maxBytesSinceLastPeriod] is accounted for.
     *
     * @param networkType Whether the thresholds applies for mobile or wifi.
     */
    data class Params(
            val maxBytesSinceAppRestart: Long,
            val maxBytesSinceLastPeriod: Long,
            val periodInMillis: Long,
            val networkType: NetworkType)

    /**
     * Callback that gets triggered when a threshold is reached
     */
    interface Callback {
        fun onMaxBytesReached(result: Result)
    }

    /**
     * Result provided by [Callback] with extra info related to this event.
     *
     * @param code Describes why the callback was triggered.
     * @param extras Extra information regarding the current the network usage.
     */
    data class Result(
            val code: ResultCode,
            val extras: Extras) {

        /**
         * Current bytes received/transmitted by this app.
         * Returns -1 if value not available.
         *
         * @param rxMobile bytes downloaded on a mobile network
         * @param txMobile bytes uploaded on a mobile network
         * @param rxWifi bytes downloaded on a wifi network
         * @param txWifi bytes uploaded on a wifi network
         * @param rxBytes bytes downloaded on all networks
         * @param txBytes bytes uploaded on all networks
         */
        data class Extras(
                val rxMobile: Long, val txMobile: Long,
                val rxWifi: Long, val txWifi: Long,
                val rxBytes: Long, val txBytes: Long,
                val estimatedBytes: Long = -1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as UsageListener

        return other.id == this.id
    }
}