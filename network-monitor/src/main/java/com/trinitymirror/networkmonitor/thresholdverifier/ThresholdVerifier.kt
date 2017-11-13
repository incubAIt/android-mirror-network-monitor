package com.trinitymirror.networkmonitor.thresholdverifier

import com.trinitymirror.networkmonitor.UsageListener

/**
 * Single-method interface to verify if traffic data was exceeded.
 * This interface (at the moment) has 2 implementations: [BaseThresholdVerifier] for `API < 23` and
 * [MarshmallowThresholdVerifier] for `API >= 23`
 *
 * @see [isThresholdReached]
 */
interface ThresholdVerifier {

    /**
     * Verifies if the threshold is exceeded for the given listener.
     */
    fun isThresholdReached(listener: UsageListener): Boolean

    /**
     * Returns a [UsageListener.Result] object that aggregates information about the current traffic stats.
     */
    fun createResult(listener: UsageListener): UsageListener.Result

}