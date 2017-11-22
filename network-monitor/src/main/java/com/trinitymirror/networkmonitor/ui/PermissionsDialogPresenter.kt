package com.trinitymirror.networkmonitor.ui

import android.app.Activity
import android.content.Intent
import com.trinitymirror.networkmonitor.NetworkMonitor
import com.trinitymirror.networkmonitor.permission.PermissionHelper

class PermissionsDialogPresenter(private val permissionHelper: PermissionHelper,
                                 private val appName: String) {

    interface View {
        fun getActivity(): Activity
        fun showUsageStatsPermissionScreen()
        fun showPhoneStatePermissionScreen()
        fun showSuccessScreen()
        fun showUsageStatsToast(appName: String)
        fun finish()
        fun requestPhoneStatePermission()
    }

    private lateinit var view: View

    fun register(view: View) {
        this.view = view
        val context = view.getActivity()

        if (permissionHelper.hasPermissions(context)) {
            onPermissionGranted(view)
        } else if (!permissionHelper.hasPermissionToReadNetworkHistory(context)) {
            view.showUsageStatsPermissionScreen()
        } else if (!permissionHelper.hasPermissionToReadPhoneState(context)) {
            view.showPhoneStatePermissionScreen()
        }
    }

    fun onGotoSettingsClicked() {
        permissionHelper.requestReadNetworkHistoryAccess(view.getActivity(), {
            PermissionsDialogActivity.reopen(view.getActivity(), appName)
        })
        view.showUsageStatsToast(appName)
    }

    fun onDismissClicked() {
        NetworkMonitor.with().onDialogDismissed()
        view.finish()
    }

    fun onNextClicked() {
        if (!permissionHelper.hasPermissionToReadPhoneState(view.getActivity())) {
            view.requestPhoneStatePermission()
        } else {
            onPermissionGranted(view)
        }
    }

    private fun onPermissionGranted(view: View) {
        view.finish()

        val intent = NetworkMonitor.with().onPermissionGranted()
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            view.getActivity().startActivity(it)
        }
    }

    fun onPhoneStatePermissionResult(phoneStateGranted: Boolean) {
        if (phoneStateGranted) {
            view.showSuccessScreen()
        } else {
            view.finish()
        }
    }
}