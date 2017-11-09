package com.trinitymirror.networkmonitor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build

/**
 * Created by ricardobelchior on 09/11/2017.
 */
internal class UsageCallbacksCompat(context: Context) : UsageCallbackRegister {

    @SuppressLint("NewApi")
    private val IMPL =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) UsageCallbackRegister.Nougat(context)
            else UsageCallbackRegister.Empty()

    override fun registerUsageCallback(listener: NetworkMonitor.UsageListener) {
        IMPL.registerUsageCallback(listener)
    }

    override fun unregisterUsageCallback(listener: NetworkMonitor.UsageListener) {
        IMPL.unregisterUsageCallback(listener)
    }

}