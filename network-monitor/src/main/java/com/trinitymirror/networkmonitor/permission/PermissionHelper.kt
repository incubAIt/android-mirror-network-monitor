package com.trinitymirror.networkmonitor.permission

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import java.lang.ref.SoftReference

class PermissionHelper {

    fun hasPermissions(context: Context): Boolean {
        return hasPermissionToReadNetworkHistory(context) &&
                hasPermissionToReadPhoneState(context)
    }

    fun hasPermissionToReadPhoneState(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun requestPhoneStatePermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_PHONE_STATE), requestCode)
    }

    fun hasPermissionToReadNetworkHistory(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true
        }

        val packageName = context.packageName
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return hasGetUsageStatsPermission(appOps, packageName)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun requestReadNetworkHistoryAccess(activity: Activity, onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onPermissionGranted.invoke()
        }

        val appOps = activity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        appOps.startWatchingMode(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                activity.packageName,
                PermissionChangedListener(appOps, onPermissionGranted))

        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivity(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    internal class PermissionChangedListener(
            private val appOps: AppOpsManager,
            permissionGrantedLambda: () -> Unit) : AppOpsManager.OnOpChangedListener {

        private val permissionGrantedWeakRef = SoftReference(permissionGrantedLambda)

        override fun onOpChanged(op: String, packageName: String) {
            permissionGrantedWeakRef.get()?.let {
                if (hasGetUsageStatsPermission(appOps, packageName)) {
                    appOps.stopWatchingMode(this)
                    it.invoke()
                }
            }
        }
    }

    companion object {

        private val myUid = Process.myUid()

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        private fun hasGetUsageStatsPermission(appOps: AppOpsManager, packageName: String): Boolean =
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, myUid, packageName) == AppOpsManager.MODE_ALLOWED
    }
}