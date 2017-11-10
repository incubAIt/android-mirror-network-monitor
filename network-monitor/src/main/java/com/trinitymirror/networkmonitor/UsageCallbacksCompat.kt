package com.trinitymirror.networkmonitor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build

/**
 * A [UsageCallbackRegister] that returns the correct implementation according to the Android version.
 */
internal class UsageCallbacksCompat(context: Context) : UsageCallbackRegister {

    @SuppressLint("NewApi")
    private val IMPL =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) UsageCallbackRegister.Nougat(context)
            else UsageCallbackRegister.Empty()

    override fun registerUsageCallback(listener: UsageListener) {
        IMPL.registerUsageCallback(listener)
    }

    override fun unregisterUsageCallback(listener: UsageListener) {
        IMPL.unregisterUsageCallback(listener)
    }

}