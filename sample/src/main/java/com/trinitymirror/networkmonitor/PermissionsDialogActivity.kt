package com.trinitymirror.networkmonitor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.trinitymirror.networkmonitor.permission.PermissionHelper

class PermissionsDialogActivity : Activity(), PermissionsDialogPresenter.View {

    companion object {
        const val EXTRA_APP_NAME = "extra_name"
        const val REQ_CODE = 1404

        @JvmStatic
        fun open(activity: Activity, appName: String) {
            activity.startActivity(
                    Intent(activity, PermissionsDialogActivity::class.java)
                            .putExtra(EXTRA_APP_NAME, appName))
        }

        fun reopen(activity: Activity, appName: String) {
            activity.finish()
            activity.startActivity(
                    Intent(activity, PermissionsDialogActivity::class.java)
                            .putExtra(EXTRA_APP_NAME, appName))
        }
    }

    private lateinit var usageStatsLayout: ConstraintLayout
    private lateinit var usageStatsTitleTextView: TextView
    private lateinit var usageStatsDescriptionTextView: TextView
    private lateinit var usageStatsDismissButton: Button
    private lateinit var usageStatsGotoSettingsButton: Button

    private lateinit var phoneStateLayout: ConstraintLayout
    private lateinit var phoneStateTitleTextView: TextView
    private lateinit var phoneStateDescriptionTextView: TextView
    private lateinit var phoneStateDismissButton: Button
    private lateinit var phoneStateNextButton: Button

    private lateinit var presenter: PermissionsDialogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_dialog)

        findViews()
        hookListeners()
        bindUi()
        initPresenter()
    }

    private fun bindUi() {
        checkMandatoryExtra(EXTRA_APP_NAME)

        val appName = getExtraAppName()
        usageStatsDescriptionTextView.text = getString(R.string.mirror_network_monitor_dialog_network_history_description, appName)
        phoneStateDescriptionTextView.text = getString(R.string.mirror_network_monitor_dialog_phone_state_description, appName)
    }

    private fun findViews() {
        usageStatsLayout = findViewById(R.id.activity_permissions_dialog_network_history_layout)
        usageStatsTitleTextView = usageStatsLayout.findViewById(R.id.activity_permissions_dialog_network_history_title)
        usageStatsDescriptionTextView = usageStatsLayout.findViewById(R.id.activity_permissions_dialog_network_history_description)
        usageStatsDismissButton = usageStatsLayout.findViewById(R.id.activity_permissions_dialog_network_history_dismiss_btn)
        usageStatsGotoSettingsButton = usageStatsLayout.findViewById(R.id.activity_permissions_dialog_network_history_settings_btn)

        phoneStateLayout = findViewById(R.id.activity_permissions_dialog_phone_state_layout)
        phoneStateTitleTextView = phoneStateLayout.findViewById(R.id.activity_permissions_dialog_phone_state_title)
        phoneStateDescriptionTextView = phoneStateLayout.findViewById(R.id.activity_permissions_dialog_phone_state_description)
        phoneStateDismissButton = phoneStateLayout.findViewById(R.id.activity_permissions_dialog_phone_state_dismiss_btn)
        phoneStateNextButton = phoneStateLayout.findViewById(R.id.activity_permissions_dialog_phone_state_next_btn)
    }

    private fun hookListeners() {
        usageStatsDismissButton.setOnClickListener { presenter.onDismissClicked() }
        usageStatsGotoSettingsButton.setOnClickListener { presenter.onGotoSettingsClicked() }

        phoneStateDismissButton.setOnClickListener { presenter.onDismissClicked() }
        phoneStateNextButton.setOnClickListener { presenter.onNextClicked() }
    }

    private fun initPresenter() {
        val appName = getExtraAppName()
        presenter = PermissionsDialogPresenter(PermissionHelper(), appName)
        presenter.register(this)
    }

    override fun showUsageStatsPermissionScreen() {
        usageStatsLayout.visibility = View.VISIBLE
        phoneStateLayout.visibility = View.GONE
    }

    override fun showPhoneStatePermissionScreen() {
        usageStatsLayout.visibility = View.GONE
        phoneStateLayout.visibility = View.VISIBLE
    }

    // change description text, hide DISMISS btn, rename NEXT btn to success
    override fun showSuccessScreen() {
        phoneStateDescriptionTextView.text = getString(R.string.mirror_network_monitor_dialog_success_description)
        phoneStateDismissButton.visibility = View.GONE
        phoneStateNextButton.text = getString(R.string.mirror_network_monitor_dialog_success)
    }

    override fun showUsageStatsToast(appName: String) {
        val toastMsg = getString(R.string.mirror_network_monitor_usage_stats_toast, appName)
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun requestPhoneStatePermission() {
        ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray) {

        val granted = requestCode == REQ_CODE &&
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED

        presenter.onPhoneStatePermissionResult(granted)
    }

    private fun getExtraAppName(): String {
        return intent.extras.getString(EXTRA_APP_NAME)
    }

    private fun checkMandatoryExtra(key: String) {
        if (!intent.extras.containsKey(key)) {
            throw IllegalArgumentException("missing mandatory key: $key")
        }
    }
}
