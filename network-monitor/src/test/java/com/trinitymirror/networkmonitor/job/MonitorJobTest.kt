package com.trinitymirror.networkmonitor.job

import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.trinitymirror.networkmonitor.*
import com.trinitymirror.networkmonitor.mother.UsageListenerMother
import com.trinitymirror.networkmonitor.persistence.JobPreferences
import com.trinitymirror.networkmonitor.thresholdverifier.ThresholdVerifier
import com.trinitymirror.networkmonitor.usagecallback.UsageCallbackRegister
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test


class MonitorJobTest : BaseTest() {

    private val jobFactory = mock(MonitorJobFactory::class)
    private val usageCallbackRegister = mock(UsageCallbackRegister::class)
    private val thresholdVerifier = mock(ThresholdVerifier::class)
    private val jobPreferences = mock(JobPreferences::class)
    private lateinit var monitorJob: MonitorJob

    @Before
    override fun setUp() {
        super.setUp()
        config.withMonitorJobFactory(jobFactory)
                .withUsageCallbackRegister(usageCallbackRegister)

        monitorJob = MonitorJob(thresholdVerifier, jobPreferences)

        whenever(thresholdVerifier.createResult(any()))
                .thenReturn(UsageListenerMother.result())
    }

    private fun runJob() {
        monitorJob.execute()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun `when execute, verify all network listeners`() {
        val listener1 = UsageListenerMother.create(1)
        val listener2 = UsageListenerMother.create(2)
        NetworkMonitor.with().registerListener(listener1)
        NetworkMonitor.with().registerListener(listener2)
        whenever(thresholdVerifier.isThresholdReached(any())).thenReturn(true)

        runJob()

        verify(thresholdVerifier).isThresholdReached(listener1)
        verify(thresholdVerifier).isThresholdReached(listener2)
    }

    @Test
    fun `when execute without listeners, do nothing`() {
        runJob()

        verify(thresholdVerifier, never()).isThresholdReached(any())
    }

    // during the same period, only notify once
    @Test
    fun `filter listeners that were triggered during last period`() {
        val listenerPeriod = 1000L
        val lastNotificationTimestamp = System.currentTimeMillis() - 500
        whenever(jobPreferences.getLastNotificationTimestamp(1)).thenReturn(lastNotificationTimestamp)
        val listener1 = UsageListenerMother.create(1, UsageListenerMother.params(periodInMillis = listenerPeriod))
        NetworkMonitor.with().registerListener(listener1)

        runJob()

        verify(thresholdVerifier, never())
                .isThresholdReached(listener1)
    }

    // after periodInMillis, start notifying again
    @Test
    fun `don't filter listeners, after period has passed`() {
        val listenerPeriod = 1000L
        val lastNotificationTimestamp = System.currentTimeMillis() - 1500
        whenever(jobPreferences.getLastNotificationTimestamp(1)).thenReturn(lastNotificationTimestamp)
        val listener1 = UsageListenerMother.create(1, UsageListenerMother.params(periodInMillis = listenerPeriod))
        NetworkMonitor.with().registerListener(listener1)

        runJob()

        verify(thresholdVerifier)
                .isThresholdReached(listener1)
    }

    @Test
    fun `when threshold is reached, store timestamp for the given listener`() {
        val listener1 = UsageListenerMother.create(1)
        NetworkMonitor.with().registerListener(listener1)
        whenever(thresholdVerifier.isThresholdReached(any())).thenReturn(true)
        whenever(jobPreferences.getLastNotificationTimestamp(listener1.id)).thenReturn(0)

        runJob()

        verify(jobPreferences)
                .setLastNotificationTimestamp(listener1.id, System.currentTimeMillis())
    }

}