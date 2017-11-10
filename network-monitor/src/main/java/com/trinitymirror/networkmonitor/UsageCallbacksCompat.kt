package com.trinitymirror.networkmonitor

import android.annotation.SuppressLint
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * A [UsageCallbackRegister] that returns the correct implementation according to the Android version.
 */
internal class UsageCallbacksCompat(private val context: Context) : UsageCallbackRegister {

    @SuppressLint("NewApi")
    private val IMPL =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) UsageCallbackRegister.Nougat(context, getNetworkStatsManager())
            else UsageCallbackRegister.Empty()

    override fun registerUsageCallback(listener: UsageListener) {
        IMPL.registerUsageCallback(listener)
    }

    override fun unregisterUsageCallback(listener: UsageListener) {
        IMPL.unregisterUsageCallback(listener)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkStatsManager() =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
}