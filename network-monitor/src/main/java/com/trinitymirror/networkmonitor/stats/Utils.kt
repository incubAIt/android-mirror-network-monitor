package com.trinitymirror.networkmonitor.stats

import android.app.usage.NetworkStats
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.telephony.TelephonyManager
import android.util.Log
import java.text.DateFormat
import java.util.*

internal object Utils {

    @RequiresApi(Build.VERSION_CODES.M)
    fun printBucket(bucket: NetworkStats.Bucket, networkType: String) {

        Log.d("TAG", String.format("Bucket[%s][rx,tx] = [%s, %s], from: %s, to: %s",
                networkType,
                formatBytes(bucket.rxBytes),
                formatBytes(bucket.txBytes),
                formatTimestamp(bucket.startTimeStamp),
                formatTimestamp(bucket.endTimeStamp)))
    }

    fun formatBytes(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return bytes.toString() + " B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "i"
        return String.format(Locale.UK, "%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    fun formatTimestamp(timestamp: Long): String {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.UK)
                .format(Date(timestamp))
    }

    /**
     * Returns the {@link TelephonyManager#subscriberId}.
     * This value is irrelevant for Wifi connections. Use it for MOBILE related queries.
     */
    fun getSubscriberId(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.subscriberId
    }

}