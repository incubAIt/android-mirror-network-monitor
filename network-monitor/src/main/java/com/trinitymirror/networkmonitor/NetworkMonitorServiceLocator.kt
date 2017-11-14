package com.trinitymirror.networkmonitor

import android.annotation.SuppressLint
import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.JobTrigger
import com.trinitymirror.networkmonitor.job.MonitorJobFactory
import com.trinitymirror.networkmonitor.persistence.JobPreferences
import com.trinitymirror.networkmonitor.persistence.JobSharedPreferences
import com.trinitymirror.networkmonitor.stats.TrafficStatsHelper
import com.trinitymirror.networkmonitor.thresholdverifier.ThresholdVerifier
import com.trinitymirror.networkmonitor.thresholdverifier.ThresholdVerifierCompat
import com.trinitymirror.networkmonitor.usagecallback.UsageCallbackRegister
import com.trinitymirror.networkmonitor.usagecallback.UsageCallbacksCompat

@SuppressLint("StaticFieldLeak")
object NetworkMonitorServiceLocator {

    internal lateinit var context: Context

    private var monitorJobFactory: MonitorJobFactory? = null
    private var usageCallbackRegister: UsageCallbackRegister? = null
    private var jobPreferences: JobPreferences? = null
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

    internal fun provideUsageCallbackRegister(): UsageCallbackRegister {
        return usageCallbackRegister ?:
                UsageCallbacksCompat(context)
                        .also { usageCallbackRegister = it }
    }

    internal fun provideThresholdVerifier(): ThresholdVerifier {
        return ThresholdVerifierCompat(context)
    }

    internal fun provideJobPreferences(): JobPreferences {
        return JobSharedPreferences(context)
    }

    internal fun provideTrafficStatsHelper(): TrafficStatsHelper {
        return TrafficStatsHelper.Impl()
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

        internal fun withMonitorJobFactory(monitorJobFactory: MonitorJobFactory): Config {
            NetworkMonitorServiceLocator.monitorJobFactory = monitorJobFactory
            return this
        }

        internal fun withUsageCallbackRegister(usageCallbackRegister: UsageCallbackRegister): Config {
            NetworkMonitorServiceLocator.usageCallbackRegister = usageCallbackRegister
            return this
        }

        internal fun withJobPreferences(jobPreferences: JobPreferences): Config {
            NetworkMonitorServiceLocator.jobPreferences = jobPreferences
            return this
        }
    }
}