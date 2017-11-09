package com.trinitymirror.networkmonitor

import android.annotation.SuppressLint
import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.JobTrigger
import com.trinitymirror.networkmonitor.monitorjob.MonitorJobFactory
import com.trinitymirror.networkmonitor.stats.NetworkStats

@SuppressLint("StaticFieldLeak")
object NetworkMonitorServiceLocator {

    internal lateinit var context: Context

    private var networkStats: NetworkStats? = null
    private var monitorJobFactory: MonitorJobFactory? = null
    private var jobExecutionPeriodicity = -1
    private var jobExecutionTolerance = -1

    internal fun provideMonitorJobFactory(): MonitorJobFactory {
        return monitorJobFactory ?:
                MonitorJobFactory(
                        FirebaseJobDispatcher(GooglePlayDriver(context)),
                        provideJobTrigger())
                        .also { monitorJobFactory = it }
    }

    private fun provideJobTrigger(): JobTrigger {
        return if (jobExecutionPeriodicity < 0) {
            MonitorJobFactory.defaultExecutionWindow()
        } else {
            MonitorJobFactory.executionWindow(jobExecutionPeriodicity, jobExecutionTolerance)
        }
    }

    internal fun provideNetworkStats(): NetworkStats {
        TODO("")
    }

    class Config(context: Context) {

        init {
            NetworkMonitorServiceLocator.context = context
        }

        fun withJobExecutionWindow(periodicity: Int, tolerance: Int): Config {
            NetworkMonitorServiceLocator.jobExecutionPeriodicity = periodicity
            NetworkMonitorServiceLocator.jobExecutionTolerance = tolerance
            return this
        }
    }
}