package com.trinitymirror.networkmonitor

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.trinitymirror.networkmonitor.job.MonitorJobFactory
import com.trinitymirror.networkmonitor.permission.PermissionHelper
import com.trinitymirror.networkmonitor.ui.PermissionsDialogActivity
import com.trinitymirror.networkmonitor.usagecallback.UsageCallbackRegister
import java.lang.ref.WeakReference

/**
 * NetworkMonitor is the main entry-point for the library, allowing users to register/unregister
 * listeners that notify whenever wifi/mobile traffic data exceeds a specified threshold.
 */
class NetworkMonitor private constructor(
        private val usageCallbacks: UsageCallbackRegister,
        private val monitorJobFactory: MonitorJobFactory,
        private val permissionHelper: PermissionHelper) {

    internal val networkListeners = mutableListOf<UsageListener>()

    private var permissionResultReference: WeakReference<PermissionDialogResult>? = null

    fun registerListener(listener: UsageListener) {
        usageCallbacks.registerUsageCallback(listener)
        networkListeners.add(listener)

        if (networkListeners.size == 1) {
            scheduleJob()
        }
    }

    fun unregisterListener(listener: UsageListener) {
        usageCallbacks.unregisterUsageCallback(listener)
        networkListeners.remove(listener)

        if (networkListeners.isEmpty()) {
            cancelJob()
        }
    }

    fun scheduleJob() {
        monitorJobFactory.scheduleJob()
    }

    fun cancelJob() {
        monitorJobFactory.cancelJob()
    }

    fun obtainCurrentStats(params: UsageListener.Params) : UsageListener.Result {
        return NetworkMonitorServiceLocator.provideThresholdVerifier()
                .createResult(params)
    }

    fun hasPermissions(context: Context)
            = permissionHelper.hasPermissions(context)

    fun openPermissionsDialog(activity: Activity, appName: String,
                              permissionDialogResult: PermissionDialogResult) {
        permissionResultReference?.clear()
        permissionResultReference = WeakReference(permissionDialogResult)

        PermissionsDialogActivity.open(activity, appName)
    }

    internal fun onDialogDismissed() {
        permissionResultReference?.apply {
            get()?.onDismissed()
            clear()
        }
    }

    internal fun onPermissionGranted(): Intent? {
        var result: Intent? = null

        permissionResultReference?.apply {
            result = get()?.onPermissionGranted()
            clear()
        }

        return result
    }

    interface PermissionDialogResult {
        fun onDismissed()
        fun onPermissionGranted(): Intent?
    }

    companion object {
        @Volatile private var INSTANCE: NetworkMonitor? = null

        @JvmStatic
        fun with(): NetworkMonitor =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: create().also { NetworkMonitor.INSTANCE = it }
                }

        private fun create(): NetworkMonitor {
            return NetworkMonitor(
                    NetworkMonitorServiceLocator.provideUsageCallbackRegister(),
                    NetworkMonitorServiceLocator.provideMonitorJobFactory(),
                    PermissionHelper())
        }

        fun reset() {
            INSTANCE = null
        }
    }
}