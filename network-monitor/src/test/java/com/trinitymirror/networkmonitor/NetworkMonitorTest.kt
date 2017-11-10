package com.trinitymirror.networkmonitor

import com.trinitymirror.networkmonitor.monitorjob.MonitorJobFactory
import com.trinitymirror.networkmonitor.mother.UsageListenerMother
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
    fun `when init, job is scheduled`() {
        NetworkMonitor.with()

        verify(jobFactory).scheduleJob()
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