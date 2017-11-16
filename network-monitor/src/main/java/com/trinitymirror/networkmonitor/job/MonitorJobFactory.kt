package com.trinitymirror.networkmonitor.job

import com.firebase.jobdispatcher.*
import java.util.concurrent.TimeUnit

/**
 * Created by ricardobelchior on 09/11/2017.
 */
internal class MonitorJobFactory(
        private val jobDispatcher: FirebaseJobDispatcher,
        private val jobTrigger: JobTrigger) {

    fun scheduleJob() {
        jobDispatcher.schedule(
                createJob(
                        jobDispatcher.newJobBuilder(),
                        jobTrigger))
    }

    fun cancelJob() {
        jobDispatcher.cancel(TAG)
    }

    companion object {
        val TAG = "network-monitor-job-service"

        private fun createJob(jobBuilder: Job.Builder, jobTrigger: JobTrigger): Job {
            return jobBuilder
                    .setService(MonitorJobService::class.java)
                    .setTag("network-monitor-job-service")
                    .setRecurring(true)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setTrigger(jobTrigger)
                    .build()
        }

        /**
         * Execute every 2h, given 4h tolerance
         */
        fun defaultExecutionWindow(): JobTrigger {
            return executionWindow(
                    TimeUnit.HOURS.toSeconds(2).toInt(),
                    TimeUnit.HOURS.toSeconds(4).toInt())
        }

        fun executionWindow(periodicity: Int, tolerance: Int): JobTrigger {
            return Trigger.executionWindow(periodicity, periodicity + tolerance)
        }

    }
}