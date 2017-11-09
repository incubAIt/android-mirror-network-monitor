package com.trinitymirror.networkmonitor.stats

import android.annotation.TargetApi
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.RemoteException

@TargetApi(Build.VERSION_CODES.M)
class NetworkStatsHelper(private val networkStatsManager: NetworkStatsManager) {

    fun queryAllRxBytesMobile(subscriberId: String): Long {
        val bucket: NetworkStats.Bucket
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    subscriberId,
                    0, System.currentTimeMillis())
        } catch (e: RemoteException) {
            return -1
        }

        return bucket.rxBytes
    }

    fun queryAllTxBytesMobile(subscriberId: String): Long {
        val bucket: NetworkStats.Bucket
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    subscriberId,
                    0,
                    System.currentTimeMillis())
        } catch (e: RemoteException) {
            return -1
        }

        return bucket.txBytes
    }

    fun queryAllRxBytesWifi(): Long {
        val bucket: NetworkStats.Bucket
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis())
        } catch (e: RemoteException) {
            return -1
        }

        return bucket.rxBytes
    }

    fun queryAllTxBytesWifi(): Long {
        val bucket: NetworkStats.Bucket
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis())
        } catch (e: RemoteException) {
            return -1
        }

        return bucket.txBytes
    }

    fun queryPackageRxBytesMobile(packageUid: Int, subscriberId: String, periodInMillis: Long): Long {
        val networkStats: NetworkStats
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    subscriberId,
                    System.currentTimeMillis() - periodInMillis,
                    System.currentTimeMillis(),
                    packageUid)
        } catch (e: RemoteException) {
            return -1
        }

        return getBytesFromBucket(networkStats, { it.rxBytes })
    }

    fun queryPackageTxBytesMobile(packageUid: Int, subscriberId: String, periodInMillis: Long): Long {
        val networkStats: NetworkStats
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    subscriberId,
                    System.currentTimeMillis() - periodInMillis,
                    System.currentTimeMillis(),
                    packageUid)
        } catch (e: RemoteException) {
            return -1
        }

        return getBytesFromBucket(networkStats, { it.txBytes })
    }

    fun queryPackageRxBytesWifi(packageUid: Int, periodInMillis: Long): Long {
        val networkStats: NetworkStats
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    System.currentTimeMillis() - periodInMillis,
                    System.currentTimeMillis(),
                    packageUid)
        } catch (e: RemoteException) {
            return -1
        }

        return getBytesFromBucket(networkStats, { it.rxBytes })
    }

    fun queryPackageTxBytesWifi(packageUid: Int, periodInMillis: Long): Long {
        val networkStats: NetworkStats
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    System.currentTimeMillis() - periodInMillis,
                    System.currentTimeMillis(),
                    packageUid)
        } catch (e: RemoteException) {
            return -1
        }

        return getBytesFromBucket(networkStats, { it.txBytes })
    }

    fun queryPackageBytesWifi(uid: Int, periodInMillis: Long): Long {
        return queryPackageRxBytesWifi(uid, periodInMillis) +
                queryPackageTxBytesWifi(uid, periodInMillis)
    }

    fun queryPackageBytesMobile(uid: Int, subscriberId: String, periodInMillis: Long): Long {
        return queryPackageRxBytesMobile(uid, subscriberId, periodInMillis) +
                queryPackageTxBytesMobile(uid, subscriberId, periodInMillis)
    }

    /**
     * Read every bucket from the given networkStats object and sum up all the bytes.
     */
    private fun getBytesFromBucket(networkStats: NetworkStats,
                                   getFromBucket: (bucket: NetworkStats.Bucket) -> Long): Long {
        var bytes: Long = 0
        val bucket = NetworkStats.Bucket()
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket)
            bytes += getFromBucket(bucket)
        }
        return bytes
    }


}
