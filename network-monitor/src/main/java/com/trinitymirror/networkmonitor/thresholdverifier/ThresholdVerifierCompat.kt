package com.trinitymirror.networkmonitor.thresholdverifier

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.trinitymirror.networkmonitor.NetworkMonitorServiceLocator
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.stats.CurrentTimeInMillis

/**
 * A [ThresholdVerifier] that returns the correct implementation according to the Android version.
 */
class ThresholdVerifierCompat(context: Context) : ThresholdVerifier {

    @SuppressLint("NewApi")
    private val IMPL =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) MarshmallowThresholdVerifier(context)
            else BaseThresholdVerifier(
                    NetworkMonitorServiceLocator.provideTrafficStatsHelper(),
                    NetworkMonitorServiceLocator.provideJobPreferences(),
                    CurrentTimeInMillis.SystemTime())

    override fun isThresholdReached(listener: UsageListener): Boolean {
        return IMPL.isThresholdReached(listener)
    }

    override fun createResult(params: UsageListener.Params): UsageListener.Result {
        return IMPL.createResult(params)
    }

}