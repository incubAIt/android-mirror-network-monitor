package com.trinitymirror.networkmonitor.monitorjob

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Job service class triggered by [FirebaseJobDispatcher][com.firebase.jobdispatcher.FirebaseJobDispatcher].
 * Actual work is delegated to [MonitorJob].
 */
internal class MonitorJobService : JobService() {

    private var disposable: Disposable? = null

    override fun onStartJob(jobParams: JobParameters): Boolean {
        disposable = MonitorJob()
                .execute()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        onComplete(jobParams),
                        onError(jobParams))

        return true
    }

    private fun onComplete(jobParams: JobParameters) = Action {
        jobFinished(jobParams, false)
    }

    private fun onError(jobParams: JobParameters) = Consumer<Throwable> {
        it.printStackTrace()
        jobFinished(jobParams, false)
    }

    override fun onStopJob(job: JobParameters): Boolean {
        disposable?.dispose()
        return true
    }
}