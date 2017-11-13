package com.trinitymirror.networkmonitor.job

import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.trinitymirror.networkmonitor.*
import com.trinitymirror.networkmonitor.mother.UsageListenerMother
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
    private lateinit var monitorJob: MonitorJob

    @Before
    override fun setUp() {
        super.setUp()
        config.withMonitorJobFactory(jobFactory)
                .withUsageCallbackRegister(usageCallbackRegister)
        monitorJob = MonitorJob(thresholdVerifier)
    }

    @Test
    fun `when execute, verify all network listeners`() {
        val listener1 = UsageListenerMother.create(1)
        val listener2 = UsageListenerMother.create(2)
        NetworkMonitor.with().registerListener(listener1)
        NetworkMonitor.with().registerListener(listener2)
        whenever(thresholdVerifier.isThresholdReached(any())).thenReturn(true)

        monitorJob.execute()
                .test()
                .assertNoErrors()
                .assertComplete()

        verify(thresholdVerifier).isThresholdReached(listener1)
        verify(thresholdVerifier).isThresholdReached(listener2)
    }

    @Test
    fun `when execute without listeners, do nothing`() {
        monitorJob.execute().test()

        verify(thresholdVerifier, never()).isThresholdReached(any())
    }

    //TODO
    // when threshold is reached:
    //   - notify listener
    //   - store in shared prefs?


}