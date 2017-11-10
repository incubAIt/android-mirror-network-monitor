package com.trinitymirror.networkmonitor

import android.content.Context
import org.mockito.Mockito


open class BaseTest {

    protected val context = Mockito.mock(Context::class.java)
    protected lateinit var config: NetworkMonitorServiceLocator.Config

    open fun setUp() {
        NetworkMonitor.reset()
        config = NetworkMonitorServiceLocator.Config(context)
    }
}