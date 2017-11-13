package com.trinitymirror.networkmonitor

import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.trinitymirror.networkmonitor.job.MonitorJobFactory
import com.trinitymirror.networkmonitor.mother.UsageListenerMother
import com.trinitymirror.networkmonitor.usagecallback.UsageCallbackRegister
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class NetworkMonitorTest : BaseTest() {


    private val jobFactory = mock(MonitorJobFactory::class)
    private val usageCallbackRegister = mock(UsageCallbackRegister::class)

    @Before
    override fun setUp() {
        super.setUp()
        config.withMonitorJobFactory(jobFactory)
                .withUsageCallbackRegister(usageCallbackRegister)

    }

    @Test
    fun `when register first listener, schedule job`() {
        NetworkMonitor.with().registerListener(UsageListenerMother.create())

        verify(jobFactory).scheduleJob()
    }

    @Test
    fun `when register second listener, don't schedule job`() {
        val networkMonitor = NetworkMonitor.with()
        networkMonitor.registerListener(UsageListenerMother.create(1))

        networkMonitor.registerListener(UsageListenerMother.create(2))

        verify(jobFactory, times(1)).scheduleJob()
    }

    @Test
    fun `when unregister last listener, cancel job`() {
        val networkMonitor = NetworkMonitor.with()
        val listener = UsageListenerMother.create(1)
        networkMonitor.registerListener(listener)

        networkMonitor.unregisterListener(listener)

        verify(jobFactory).cancelJob()
    }

    @Test
    fun `when unregister listener but not the last one, don't cancel job`() {
        val networkMonitor = NetworkMonitor.with()
        val listener1 = UsageListenerMother.create(1)
        val listener2 = UsageListenerMother.create(2)
        networkMonitor.registerListener(listener1)
        networkMonitor.registerListener(listener2)

        networkMonitor.unregisterListener(listener1)

        verify(jobFactory, never()).cancelJob()
    }

    @Test
    fun `when register, invoke usageCallbackRegister and add listener`() {
        val listener = UsageListenerMother.create()
        val networkMonitor = NetworkMonitor.with()
        networkMonitor.registerListener(listener)

        verify(usageCallbackRegister).registerUsageCallback(listener)
        networkMonitor.networkListeners.size shouldEqual 1
        networkMonitor.networkListeners shouldContain listener
    }

    @Test
    fun `when unregister, invoke usageCallbackRegister and remove listener`() {
        val listener = UsageListenerMother.create()
        val networkMonitor = NetworkMonitor.with()
        networkMonitor.registerListener(listener)
        networkMonitor.unregisterListener(listener)

        verify(usageCallbackRegister).unregisterUsageCallback(listener)
        networkMonitor.networkListeners.shouldBeEmpty()
    }


}