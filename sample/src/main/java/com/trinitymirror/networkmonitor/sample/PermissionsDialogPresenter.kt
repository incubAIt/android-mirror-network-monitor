package com.trinitymirror.networkmonitor.sample

import android.app.Activity
import android.content.Intent
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
            view.finish()
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


        //TODO Watch-out for memory leaks, when user runs away from the settings screen
    }

    fun onDismissClicked() {
        view.finish()
    }

    fun onNextClicked() {
        if (!permissionHelper.hasPermissionToReadPhoneState(view.getActivity())) {
            view.requestPhoneStatePermission()
        } else {
            view.finish()
            view.getActivity()
                    .startActivity(
                            Intent(view.getActivity(), SampleActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
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