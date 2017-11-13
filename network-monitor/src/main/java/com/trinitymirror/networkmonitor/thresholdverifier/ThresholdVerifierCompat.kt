package com.trinitymirror.networkmonitor.thresholdverifier

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.trinitymirror.networkmonitor.UsageListener

/**
 * A [ThresholdVerifier] that returns the correct implementation according to the Android version.
 */
class ThresholdVerifierCompat(context: Context) : ThresholdVerifier {

    @SuppressLint("NewApi")
    private val IMPL =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) MarshmallowThresholdVerifier(context)
            else BaseThresholdVerifier()

    override fun isThresholdReached(listener: UsageListener): Boolean {
        return IMPL.isThresholdReached(listener)
    }

    override fun createResult(listener: UsageListener): UsageListener.Result {
        return IMPL.createResult(listener)
    }

}