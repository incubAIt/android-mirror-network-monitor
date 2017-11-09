package com.trinitymirror.networkmonitor.monitorjob

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.trinitymirror.networkmonitor.NetworkMonitor

/**
 * Created by ricardobelchior on 09/11/2017.
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