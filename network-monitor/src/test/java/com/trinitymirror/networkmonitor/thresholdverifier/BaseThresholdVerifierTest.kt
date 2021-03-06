package com.trinitymirror.networkmonitor.thresholdverifier

import com.nhaarman.mockito_kotlin.whenever
import com.trinitymirror.networkmonitor.BaseTest
import com.trinitymirror.networkmonitor.UsageListener
import com.trinitymirror.networkmonitor.mother.UsageListenerMother
import com.trinitymirror.networkmonitor.mother.UsageListenerMother.params
import com.trinitymirror.networkmonitor.persistence.InMemoryPreferences
import com.trinitymirror.networkmonitor.stats.CurrentTimeInMillis
import com.trinitymirror.networkmonitor.stats.TrafficStatsHelper
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

internal class BaseThresholdVerifierTest : BaseTest() {

    val maxBytesSinceLastPeriod: Long = 100
    val periodInMillis: Long = 6

    val currentTime = mock(CurrentTimeInMillis::class)
    val trafficStatsHelper = mock(TrafficStatsHelper::class)

    lateinit var thresholdVerifier: ThresholdVerifier
    lateinit var listener: UsageListener

    @Before
    override fun setUp() {
        super.setUp()

        init()
    }

    fun init(period: Long = periodInMillis, maxBytes: Long = maxBytesSinceLastPeriod) {
        thresholdVerifier = BaseThresholdVerifier(
                trafficStatsHelper, InMemoryPreferences(), currentTime)

        listener = UsageListenerMother.create(1,
                params(maxBytesSinceLastPeriod = maxBytes, periodInMillis = period))
    }

    @Test
    fun `test threshold not reached`() {
        whenever(currentTime.obtain()).thenReturn(1)
        whenever(trafficStatsHelper.uidBytes(any())).thenReturn(40)

        assertFalse(
                thresholdVerifier.isThresholdReached(listener))
    }

    @Test
    fun `test threshold reached`() {
        whenever(currentTime.obtain()).thenReturn(1)
        whenever(trafficStatsHelper.uidBytes(any())).thenReturn(120)

        assertTrue(
                thresholdVerifier.isThresholdReached(listener))
    }

    /**
     * simulate series of values returned by [android.net.TrafficStats] over time, to test whether
     * [BaseThresholdVerifier] correctly decides if threshold was reached or not.
     *
     * This test also simulates device reboots, which resets to `0`
     * all values returned [android.net.TrafficStats].
     *
     * Help document here:
     * [https://docs.google.com/a/trinitymirror.com/spreadsheets/d/12AI2XHAmE8GuoZPkE5iC_60Gd89nwHqjIivNAJ8CCnw]
     */
    @Test
    fun integrationTest() {
        verifyWhen(0, 10, false)    // total = 0
        verifyWhen(1, 20, false)    // total = 10
        verifyWhen(2, 80, false)    // total = 70
        verifyWhen(3, 20, false)    // total = 90
        verifyWhen(4, 40, true)     // total = 110
        verifyWhen(5, 50, true)     // total = 120
        verifyWhen(6, 60, false)    // total = 0
        verifyWhen(7, 70, false)    // total = 10
        verifyWhen(8, 100, false)   // total = 40
        verifyWhen(9, 110, false)   // total = 50
        verifyWhen(10, 20, false)   // total = 70
        verifyWhen(11, 60, true)    // total = 110
        verifyWhen(12, 0, false)    // total = 0
        verifyWhen(13, 0, false)    // total = 0
        verifyWhen(14, 10, false)   // total = 10
    }

    @Test
    fun `test with real timestamps`() {
        init(period = TimeUnit.DAYS.toMillis(1))

        calculateWhen("16 November 2017 15:23:18 WET", 0, 0)
        calculateWhen("16 November 2017 15:23:30 WET", 965, 965)
        calculateWhen("16 November 2017 15:24:03 WET", 965, 965)
    }

    fun verifyWhen(currentTimeValue: Long, currentTraffic: Long, isThresholdReached: Boolean) {
        (thresholdVerifier as BaseThresholdVerifier).totalBytesSinceLastPeriod = -1

        whenever(currentTime.obtain()).thenReturn(currentTimeValue)
        whenever(trafficStatsHelper.uidBytes(any())).thenReturn(currentTraffic)
        assertEquals("expected thresholdReached=$isThresholdReached, when currentTime=$currentTimeValue and currentTraffic=$currentTraffic",
                isThresholdReached, thresholdVerifier.isThresholdReached(listener))
    }

    fun calculateWhen(currentTimeValue: String, currentTraffic: Long, expectedResult: Long) {
        (thresholdVerifier as BaseThresholdVerifier).totalBytesSinceLastPeriod = -1

        whenever(currentTime.obtain()).thenReturn(parseTimestamp(currentTimeValue))
        whenever(trafficStatsHelper.uidBytes(any())).thenReturn(currentTraffic)
        assertEquals("expected expectedResult=$expectedResult, when currentTime=$currentTimeValue and currentTraffic=$currentTraffic",
                expectedResult, thresholdVerifier.createResult(listener.params).extras.estimatedBytes)
    }

    fun parseTimestamp(dateTime: String): Long {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.UK)
                .parse(dateTime).time
    }
}