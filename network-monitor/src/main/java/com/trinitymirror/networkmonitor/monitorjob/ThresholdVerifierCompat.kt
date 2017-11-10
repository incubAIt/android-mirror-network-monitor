package com.trinitymirror.networkmonitor.monitorjob

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.trinitymirror.networkmonitor.NetworkMonitor

/**
 * A [ThresholdVerifier] that returns the correct implementation according to the Android version.
 */
class ThresholdVerifierCompat(context: Context) : ThresholdVerifier {

    @SuppressLint("NewApi")
    private val IMPL =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) ThresholdVerifier.MarshmallowThresholdVerifier(context)
            else ThresholdVerifier.BaseThresholdVerifier()

    override fun isThresholdReached(listener: NetworkMonitor.UsageListener): ThresholdVerifier.Result {
        return IMPL.isThresholdReached(listener)
    }
}