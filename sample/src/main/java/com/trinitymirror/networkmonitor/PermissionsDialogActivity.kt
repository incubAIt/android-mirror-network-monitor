package com.trinitymirror.networkmonitor

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class PermissionsDialogActivity : Activity() {

    lateinit var titleTextView: TextView
    lateinit var dismissButton: Button
    lateinit var gotoSettingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_dialog)

        findViews()
        hookListeners()
    }

    private fun findViews() {
        titleTextView = findViewById(R.id.dialog_title)
        dismissButton = findViewById(R.id.dismiss_btn)
        gotoSettingsButton = findViewById(R.id.settings_btn)
    }

    private fun hookListeners() {
        dismissButton.setOnClickListener { onDismissClicked() }
        gotoSettingsButton.setOnClickListener { onGotoSettingsClicked() }
    }

    private fun onDismissClicked() {
        finish()
    }

    private fun onGotoSettingsClicked() {

    }
}
